package common.protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.event.ChatMessageEvent;
import common.event.UserConnectedEvent;
import common.event.UserDisconnectedEvent;
import common.request.ListUsersRequest;
import common.request.LoginRequest;
import common.request.LogoutRequest;
import common.request.MessageRequest;
import common.request.PingRequest;
import common.response.ErrorResponse;
import common.response.ListUsersResponse;
import common.response.LoginResponse;
import common.response.LogoutResponse;
import common.response.MessageResponse;
import common.response.PingResponse;

public class XmlProtocol implements Protocol {

    private final DataInputStream in;
    private final DataOutputStream out;
    private final DocumentBuilder builder;

    public XmlProtocol(InputStream in, OutputStream out) {
        this.in = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
        this.out = out instanceof DataOutputStream ? (DataOutputStream) out : new DataOutputStream(out);
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
    }

    @Override
    public void sendDatagram(Datagram msg) throws ConnectionLostException {
        try {
            byte[] body = toXml(msg).getBytes(StandardCharsets.UTF_8);
            out.writeInt(body.length);
            out.write(body);
            out.flush();
        } catch (IOException | UnsupportedProtocolException e) {
            throw new ConnectionLostException();
        }
    }

    private String toXml(Datagram msg) throws UnsupportedProtocolException {
        if (msg instanceof LoginRequest r) {
            return command("login", elem("name", r.getUserName()) + elem("type", r.getClientName()));
        } else if (msg instanceof ListUsersRequest r) {
            return command("list", elem("session", r.getSessionId()));
        } else if (msg instanceof MessageRequest r) {
            return command("message", elem("message", r.getText()) + elem("session", r.getSessionId()));
        } else if (msg instanceof LogoutRequest r) {
            return command("logout", elem("session", r.getSessionId()));
        } else if (msg instanceof PingRequest r) {
            return command("ping", elem("session", r.getSessionId()));
        } else if (msg instanceof LoginResponse r) {
            return success(elem("session", r.sessionId));
        } else if (msg instanceof ListUsersResponse r) {
            StringBuilder users = new StringBuilder();
            for (String name : r.usersList) {
                users.append("<user>").append(elem("name", name)).append("</user>");
            }
            return success("<listusers>" + users + "</listusers>");
        } else if (msg instanceof MessageResponse) {
            return "<success></success>";
        } else if (msg instanceof LogoutResponse) {
            return success("<logout></logout>");
        } else if (msg instanceof PingResponse) {
            return success("<ping></ping>");
        } else if (msg instanceof ErrorResponse r) {
            return "<error>" + elem("message", r.reason) + "</error>";
        } else if (msg instanceof ChatMessageEvent e) {
            return event("message", elem("message", e.getText()) + elem("name", e.getFrom()));
        } else if (msg instanceof UserConnectedEvent e) {
            return event("userlogin", elem("name", e.getUserName()));
        } else if (msg instanceof UserDisconnectedEvent e) {
            return event("userlogout", elem("name", e.getUserName()) + elem("reason", e.getReason()));
        }
        throw new UnsupportedProtocolException();
    }

    private static String command(String name, String body) {
        return "<command name=\"" + name + "\">" + body + "</command>";
    }

    private static String event(String name, String body) {
        return "<event name=\"" + name + "\">" + body + "</event>";
    }

    private static String success(String body) {
        return "<success>" + body + "</success>";
    }

    private static String elem(String tag, String value) {
        return "<" + tag + ">" + escape(value) + "</" + tag + ">";
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @Override
    public Datagram receiveDatagram() throws UnsupportedProtocolException, ConnectionLostException {
        try {
            int length = in.readInt();
            byte[] xmlBytes = in.readNBytes(length);
            if (xmlBytes.length != length) {
                throw new ConnectionLostException();
            }

            var doc = builder.parse(new ByteArrayInputStream(xmlBytes));
            Element root = doc.getDocumentElement();
            String tag = root.getTagName();
            String name = root.getAttribute("name");

            return switch (tag) {
                case "command" -> parseCommand(root, name);
                case "success" -> parseSuccess(root);
                case "error" -> new ErrorResponse(text(root, "message"));
                case "event" -> parseEvent(root, name);
                default -> throw new UnsupportedProtocolException();
            };
        } catch (IOException e) {
            throw new ConnectionLostException();
        } catch (SAXException e) {
            throw new UnsupportedProtocolException();
        }
    }

    private Datagram parseCommand(Element root, String name) throws UnsupportedProtocolException {
        return switch (name) {
            case "login" -> new LoginRequest(text(root, "name"), text(root, "type"));
            case "list" -> new ListUsersRequest(text(root, "session"));
            case "message" -> new MessageRequest(text(root, "session"), text(root, "message"));
            case "logout" -> new LogoutRequest(text(root, "session"));
            case "ping" -> new PingRequest(text(root, "session"));
            default -> throw new UnsupportedProtocolException();
        };
    }

    private Datagram parseSuccess(Element root) {
        if (root.getElementsByTagName("session").getLength() > 0) {
            return new LoginResponse(text(root, "session"));
        }
        if (root.getElementsByTagName("listusers").getLength() > 0) {
            return new ListUsersResponse(parseUserNames(root));
        }
        if (root.getElementsByTagName("ping").getLength() > 0) {
            return new PingResponse();
        }
        if (root.getElementsByTagName("logout").getLength() > 0) {
            return new LogoutResponse();
        }
        return new MessageResponse();
    }

    private List<String> parseUserNames(Element root) {
        NodeList users = root.getElementsByTagName("user");
        List<String> names = new ArrayList<>(users.getLength());
        for (int i = 0; i < users.getLength(); i++) {
            names.add(text((Element) users.item(i), "name"));
        }
        return names;
    }

    private Datagram parseEvent(Element root, String name) throws UnsupportedProtocolException {
        return switch (name) {
            case "message" -> new ChatMessageEvent(text(root, "message"), text(root, "name"));
            case "userlogin" -> new UserConnectedEvent(text(root, "name"));
            case "userlogout" -> new UserDisconnectedEvent(text(root, "name"), text(root, "reason"));
            default -> throw new UnsupportedProtocolException();
        };
    }

    private static String text(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0) {
            return "";
        }
        return nodes.item(0).getTextContent().trim();
    }
}
