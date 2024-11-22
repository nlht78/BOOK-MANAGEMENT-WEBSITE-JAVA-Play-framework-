package controllers;

import models.Book;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;


import views.html.*;
import javax.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import play.libs.Json;
import views.html.books.*;
public class BooksController extends Controller {

    private final FormFactory formFactory;
    private final MessagesApi messagesApi;
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("defaultPersistenceUnit");

    @Inject
    public BooksController(FormFactory formFactory, MessagesApi messagesApi) {
        this.formFactory = formFactory;
        this.messagesApi = messagesApi;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    
    
    public Result index() {
        EntityManager em = getEntityManager();
        List<Book> booksList = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        Set<Book> booksSet = new HashSet<>(booksList); // Chuyển đổi List thành Set
        em.close();
        return ok(book.render(booksSet)); // Truyền Set vào view
    }
    


    public Result create(Http.Request request) {
        Form<Book> bookForm = formFactory.form(Book.class);
        return ok(create.render(bookForm, messagesApi.preferred(request)));
    }

    
    public Result save(Http.Request request) {
        Form<Book> bookForm = formFactory.form(Book.class).bindFromRequest(request);
        if (bookForm.hasErrors()) {
            return badRequest(create.render(bookForm, messagesApi.preferred(request)));
        }

        Book book = bookForm.get();
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.merge(book); // Thay đổi từ persist thành merge
        em.getTransaction().commit();
        em.close();
        
        return redirect(routes.BooksController.index());
    }


    public Result edit(Integer id, Http.Request request) {
        EntityManager em = getEntityManager();
        Book book = em.find(Book.class, id);
        em.close();

        if (book == null) {
            return notFound("Book Not Found");
        }

        Form<Book> bookForm = formFactory.form(Book.class).fill(book);
        return ok(edit.render(bookForm, messagesApi.preferred(request)));
    }

    public Result update(Http.Request request) {
        Form<Book> bookForm = formFactory.form(Book.class).bindFromRequest(request);
        if (bookForm.hasErrors()) {
            return badRequest(edit.render(bookForm, messagesApi.preferred(request)));
        }

        Book book = bookForm.get();
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Book existingBook = em.find(Book.class, book.getId());
        if (existingBook != null) {
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setPrice(book.getPrice());
            em.getTransaction().commit();
        }
        em.close();

        return redirect(routes.BooksController.index());
    }

    public Result destroy(Integer id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
            em.getTransaction().commit();
        }
        em.close();

        return redirect(routes.BooksController.index());
    }
    public Result show(Integer id) {
        EntityManager em = getEntityManager();
        Book book = em.find(Book.class, id);
        em.close();

        if (book == null) {
            return notFound("Book Not Found");
        }

        return ok(show.render(book));
    }
    //RESTFUL API
    
    public Result getallbook() {
        EntityManager em = getEntityManager();
        List<Book> booksList = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        em.close();
        return ok(Json.toJson(booksList));  // Trả về JSON
    }
    
    public Result saveapi(Http.Request request) {
        Form<Book> bookForm = formFactory.form(Book.class).bindFromRequest(request);
        if (bookForm.hasErrors()) {
            return badRequest(Json.toJson("Error: Invalid Book Data"));
        }

        Book book = bookForm.get();
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.merge(book);
        em.getTransaction().commit();
        em.close();
        
        return created(Json.toJson(book));  // Trả về JSON sau khi tạo thành công
    }
    public Result showapi(Integer id) {
        EntityManager em = getEntityManager();
        Book book = em.find(Book.class, id);
        em.close();

        if (book == null) {
            return notFound(Json.toJson("Book Not Found"));
        }

        return ok(Json.toJson(book));  // Trả về JSON
    }
    
    public Result updateapi(Integer id, Http.Request request) {
        Form<Book> bookForm = formFactory.form(Book.class).bindFromRequest(request);
        if (bookForm.hasErrors()) {
            return badRequest(Json.toJson("Error: Invalid Book Data"));
        }

        Book updatedBookData = bookForm.get();
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Book existingBook = em.find(Book.class, id);
        
        if (existingBook != null) {
            // Cập nhật thông tin sách hiện có
            existingBook.setTitle(updatedBookData.getTitle());
            existingBook.setAuthor(updatedBookData.getAuthor());
            existingBook.setPrice(updatedBookData.getPrice());
            em.getTransaction().commit();
            em.close();
            return ok(Json.toJson(existingBook));  // Trả về JSON của sách đã cập nhật
        } else {
            em.getTransaction().rollback();
            em.close();
            return notFound(Json.toJson("Book Not Found"));
        }
    }
    public Result destroyapi(Integer id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Book book = em.find(Book.class, id);
        
        if (book != null) {
            em.remove(book);
            em.getTransaction().commit();
            em.close();
            return ok(Json.toJson("Book deleted successfully"));  // Trả về thông báo JSON khi xóa thành công
        } else {
            em.getTransaction().rollback();
            em.close();
            return notFound(Json.toJson("Book Not Found"));
        }
    }
    
    
    
    
    
    

}
