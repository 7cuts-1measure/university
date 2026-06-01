#!/usr/bin/env fish

for i in (seq 10)
    fish -c "
        set data 'Hello from  $i'
        echo \"\$data\" | nc -U /tmp/mysocket
    " &
end