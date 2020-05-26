package com.diplom.work.core.json.view;

public class Views {
    public interface onlyId{}
    public interface forTable extends onlyId {}
    public interface simpleObject extends forTable {}
    public interface allRule extends simpleObject {}
    public interface allClient extends simpleObject {}
    public interface allLog extends simpleObject {}
    public interface allUser extends simpleObject {}
}
