package com.dao;

import java.util.List;

import org.hibernate.Session;

import com.entity.Products;
import com.util.HibernateUtil;



public class ProductDao {
    public void saveProducts(Products Products) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(Products);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving Product: " + e.getMessage());
        }
    }

    public List<Products> getAllProductss() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Products", Products.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Products: " + e.getMessage());
        }
    }

    public Products getProducts(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Products.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Product: " + e.getMessage());
        }
    }

    public void updateProducts(Products Products) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(Products);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating Products: " + e.getMessage());
        }
    }

    public void deleteProducts(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Products Products = session.get(Products.class, id);
            if (Products != null) {
                session.remove(Products);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting Product: " + e.getMessage());
        }
    }
}
