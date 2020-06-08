package com.diplom.work.core.json.view;

public class Views {
    public interface OnlyId {}
    public interface ForTable extends OnlyId {}
    public interface SimpleObject extends ForTable {}
    public interface AllRule extends SimpleObject {}
    public interface AllClient extends SimpleObject {}
    public interface AllLog extends SimpleObject {}
    public interface AllUser extends SimpleObject {}
}
