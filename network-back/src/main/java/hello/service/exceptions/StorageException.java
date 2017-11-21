package hello.service.exceptions;

import java.io.IOException;

public class StorageException extends Throwable {
    public StorageException(String s) {
        super(s);
    }

    public StorageException(String s, Exception e) {
        super(s,e);
    }
}
