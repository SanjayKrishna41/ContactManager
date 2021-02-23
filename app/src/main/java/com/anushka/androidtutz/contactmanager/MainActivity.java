package com.anushka.androidtutz.contactmanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.anushka.androidtutz.contactmanager.adapter.ContactsAdapter;
import com.anushka.androidtutz.contactmanager.db.ContactsAppDatabase;
import com.anushka.androidtutz.contactmanager.db.entity.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
//    private DatabaseHelper db;
    private ContactsAppDatabase contactsAppDatabase;
    CompositeDisposable compositeDisposable = new CompositeDisposable();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Contacts Manager");

        recyclerView = findViewById(R.id.recycler_view_contacts);
        contactsAppDatabase = Room.databaseBuilder(getApplicationContext(),ContactsAppDatabase.class,"ContactDB").allowMainThreadQueries().build();
//        db = new DatabaseHelper(this);

//        contactArrayList.addAll(contactsAppDatabase.getContactDAO().getAllContacts());


        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);

       compositeDisposable.add( contactsAppDatabase.getContactDAO().getAllContactsUsingRx()
               .subscribeOn(Schedulers.computation())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Consumer<List<Contact>>() {
                              @Override
                              public void accept(List<Contact> contacts) throws Exception {
                                  contactArrayList.clear();
                                  contactArrayList.addAll(contacts);
                                  contactsAdapter.notifyDataSetChanged();
                              }
                          }, new Consumer<Throwable>() {
                              @Override
                              public void accept(Throwable throwable) throws Exception {

                              }
                          }
               )
       );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditContacts(false, null, -1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addAndEditContacts(final boolean isUpdate, final Contact contact, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView contactTitle = view.findViewById(R.id.new_contact_title);
        final EditText newContact = view.findViewById(R.id.name);
        final EditText contactEmail = view.findViewById(R.id.email);

        contactTitle.setText(!isUpdate ? "Add New Contact" : "Edit Contact");

        if (isUpdate && contact != null) {
            newContact.setText(contact.getName());
            contactEmail.setText(contact.getEmail());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                if (isUpdate) {

                                    deleteContact(contact, position);
                                } else {

                                    dialogBox.cancel();

                                }

                            }
                        });


        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(newContact.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }


                if (isUpdate && contact != null) {

                    updateContact(newContact.getText().toString(), contactEmail.getText().toString(), position);
                } else {

                    createContact(newContact.getText().toString(), contactEmail.getText().toString());
                }
            }
        });
    }

    private void deleteContact(Contact contact, int position) {

//        contactArrayList.remove(position);
        contactsAppDatabase.getContactDAO().deleteContact(contact);
//        contactsAdapter.notifyDataSetChanged();
    }

    private void updateContact(String name, String email, int position) {

        Contact contact = contactArrayList.get(position);

        contact.setName(name);
        contact.setEmail(email);

        contactsAppDatabase.getContactDAO().updateContact(contact);

//        contactArrayList.set(position, contact);
//
//        contactsAdapter.notifyDataSetChanged();


    }

    private void createContact(String name, String email) {

        //for auto generated values add zero
        long id = contactsAppDatabase.getContactDAO().addContact(new Contact(0,name,email));


//        Contact contact = contactsAppDatabase.getContactDAO().getContactBasedOnId(id);
//
//        if (contact != null) {
//
//            contactArrayList.add(0, contact);
//            contactsAdapter.notifyDataSetChanged();
//
//        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

}
