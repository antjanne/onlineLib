package map.anton.books;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Book {

    private String title;
    private int nrPages;
    private String isbn;

    public Book(String title, int nrPages, String isbn) {
        this.title = title;
        this.nrPages = nrPages;
        this.isbn = isbn;
    }



    public String getTitle() {
        return title;
    }

    public int getNrPages() {
        return nrPages;
    }

}
