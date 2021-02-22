package com.anushka.androidtutz.contactmanager.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.anushka.androidtutz.contactmanager.db.entity.Contact;

import java.util.List;

@Dao
public interface ContactDAO
{
    @Insert
    public long addContact(Contact contact);

    @Update
    public void updateContact(Contact contact);

    @Delete
    public void deleteContact(Contact contact);

    @Query("select * from contacts")
    public List<Contact> getAllContacts();

    @Query("select * from contacts where contact_id == :contact_id")
    public Contact getContactBasedOnId(long contact_id);
}
