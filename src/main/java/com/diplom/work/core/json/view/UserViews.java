package com.diplom.work.core.json.view;

public class UserViews {
    public interface onlyId{}
    public interface idLogin extends onlyId{}
    public interface forTable extends idLogin{}
    public interface withoutPassword extends forTable{}
    public interface all extends withoutPassword{}
}
