package map.anton.books;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ControllBooks {

    RequestQueue requestQueue;



    public ControllBooks(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }


    
}
