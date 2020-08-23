package map.anton.books;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;

    String url = "https://openlibrary.org/api/books?bibkeys=ISBN:";
    String lastUrl = "&jscmd=data&title&format=json";
    TextView view;
    Button sButton;
    TextInputEditText isbnCode;
    String isbn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this); // Will take care of background network activities.
        view = findViewById(R.id.ruta);
        sButton = findViewById(R.id.searchButton);

        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isbnCode = findViewById(R.id.isbnCodebox);
                isbn = isbnCode.getText().toString();
                getBook();
            }
        });
    }
    public void getBook()
    {

        String totalUrl = url + isbn + lastUrl;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, totalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonObject = response;
                try{
                    JSONObject book = jsonObject.getJSONObject("ISBN:"+isbn);
                    String booktitle = book.getString("title");
                    String pages;
                    int pagesNumbers = 0;
                    if(book.has("number_of_pages")){
                        pages = book.getString("number_of_pages");
                        pagesNumbers = Integer.valueOf(pages);
                    }
                    else if(book.has("pagination")){
                        pages = book.getString("pagination");
                        pagesNumbers = Integer.valueOf(pages);
                    }
                    else{
                        pages = "Not available";
                    }

                    view.setText(booktitle + "," + pages);
                }
                catch (Exception w)
                {
                    Toast.makeText(MainActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.getMessage() + "HATASTASDWDA",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}

