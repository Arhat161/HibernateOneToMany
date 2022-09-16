package org.example;

import org.example.model.Item;
import org.example.model.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Hibernate - One-To-Many
 * You can uncomment blocks of code for run some examples.
 * If you want cansel any changes in database, run code from file 'schema.sql'
 */
public class App {
    public static void main(String[] args) {
        // First, create Configuration (all connection properties in 'hibernate.properties' file
        Configuration configuration = new Configuration().addAnnotatedClass(Person.class).addAnnotatedClass(Item.class);
        // Second, create sessionFactory
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        // Third, get session to wor from sessionFactory
        Session session = sessionFactory.getCurrentSession();

        try {
            // open transaction
            session.beginTransaction();

            // get person and all his items
            // 1 - get person
            Person person = session.get(Person.class, 3);
            System.out.println(person);
            // 2 - get person items
            List<Item> items = person.getItems();
            System.out.println(items);

            // get item and get owner of this item
            // 1 - get item
            Item item = session.get(Item.class, 5);
            System.out.println(item);
            // 2 - get owner of this item
            Person itemOwner = item.getOwner();
            System.out.println(itemOwner);


            // add item for person
            // 1 - get person
            Person personForAddItem = session.get(Person.class, 2);
            // 2 - create new item and set in item 'person_id' = 2
            Item newItem = new Item("Item from Hibernate", personForAddItem);
            // 3 - add item in list of person items (because Hibernate cashing results of query)
            personForAddItem.getItems().add(newItem);
            // 4 - save
            session.save(newItem);


            // add one person and one item for this person
            // 1 - add new person
            Person newPerson = new Person("Mike", 30);
            // 2 - add new item when owner is newPerson
            Item itemForNewPerson = new Item("Item For New Person", person);
            // 3 - add new item to list of persons items (because Hibernate cashing results of query)
            newPerson.setItems(new ArrayList<>(Collections.singletonList(itemForNewPerson)));
            // 4 - save newPerson
            session.save(newPerson);
            // 5 - save itemForNewPerson
            session.save(itemForNewPerson);

            // delete all items from table 'Item', where owner id = 3
            // 1 - get person from table 'Person'
            Person anotherPerson = session.get(Person.class, 3);
            // 2 - get list of all items of anotherPerson
            List<Item> itemsForDelete = anotherPerson.getItems();
            // 3 - delete from table in database
            for (Item oneItem : itemsForDelete) {
                session.remove(oneItem);
            }
            // 4 - clear list items on person (because Hibernate cashing results of query)
            anotherPerson.getItems().clear();

            // delete person from table in database
            // 1 - get person from table in database
            Person personForDelete = session.get(Person.class, 2);
            // 2 - delete record from table in database
            session.remove(personForDelete);
            // 3 - after delete from database, we set owner to null for each item, where person_id was 2
            // (because Hibernate cashing results of query)
            personForDelete.getItems().forEach(i -> i.setOwner(null));


            // chance owner of item
            // 1 - get person from table in database, where id = 3
            Person ownerPerson = session.get(Person.class, 3);
            // 2 - get item from table in database, where id = 1
            Item toChangeOwnerItem = session.get(Item.class, 1);
            // 3 - change 'person_id' to item toChangeOwnerItem (change owner from 1 to 3)
            toChangeOwnerItem.setOwner(ownerPerson);
            // 4 - add item 'toChangeOwnerItem' in item list on ownerPerson (because Hibernate cashing results of query)
            ownerPerson.getItems().add(toChangeOwnerItem);

            // close transaction
            session.getTransaction().commit();

        } finally {
            // close sessionFactory
            sessionFactory.close();
        }
    }
}
