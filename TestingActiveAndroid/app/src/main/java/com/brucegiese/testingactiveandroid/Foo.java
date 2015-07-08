package com.brucegiese.testingactiveandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="Foo")
public class Foo extends Model {

    @Column(name="User")
    public String mUser;


    @SuppressWarnings("unused")
    public Foo() {
        super();
    }

    public Foo( String user ) {
        super();
        mUser = user;
    }
}
