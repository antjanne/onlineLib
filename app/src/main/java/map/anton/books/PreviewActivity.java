package map.anton.books;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PreviewActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};



    RequestQueue requestQueue;

    private PreviewView mPreviewView;
    List<Book> books = new ArrayList<>();

    private Analyzer analyzer = new Analyzer();
    private RecyclerView.Adapter mAdapter;
    private Button scanButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_view);
        mPreviewView = findViewById(R.id.previewView);
        scanButton = findViewById(R.id.getBook);
        requestQueue = Volley.newRequestQueue(this);

        scanButton.setOnClickListener(view -> {
           String isbn =  analyzer.getIsbns().get(0);
           getBook(isbn);

        });


        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(this,"DU LYCKADES", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();


        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(Executors.newFixedThreadPool(1),analyzer);
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());


        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);

    }
    private void viewBooks(List<Book> books) {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new BookAdapter(books);  //Adapter takes the list of books and places them in the recycleview.
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    public void getBook(String isbn)
    {
        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:";
        String lastUrl = "&jscmd=data&title&format=json";

        String totalUrl = url + isbn + lastUrl;
        Log.d(totalUrl, "HORA");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, totalUrl, null, response -> {
            JSONObject jsonObject = response;
            try{
                JSONObject book = jsonObject.getJSONObject("ISBN:"+isbn);
                String booktitle = book.getString("title");
                Log.v(booktitle, "HORA");
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
                Book bok = new Book(booktitle,pagesNumbers,isbn);
                books.add(bok);
                viewBooks(books);
            }
            catch (Exception w)
            {
                Toast.makeText(PreviewActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
            }
        }, error -> Log.v("Couldn't fetch data", "rip"));
        requestQueue.add(jsonObjectRequest);
    }
}
