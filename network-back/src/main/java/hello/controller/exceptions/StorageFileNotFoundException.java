package hello.controller.exceptions;

public class StorageFileNotFoundException extends Throwable {
    StorageFileNotFoundException(String msg){
        super(msg);
    }
}
