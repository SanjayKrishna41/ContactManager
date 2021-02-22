package com.anushka.androidtutz.contactmanager.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.anushka.androidtutz.contactmanager.db.entity.Contact;

@Database(entities = {Contact.class},version = 1)
public abstract class ContactsAppDatabase extends RoomDatabase
{
    //we created this abstract method to invoke all the methods inside ContactDAO interface
    public abstract ContactDAO getContactDAO();
}
