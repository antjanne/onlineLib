package map.anton.books;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.internal.Experimental;
import com.google.android.material.textfield.TextInputEditText;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.common.internal.VisionImageMetadataParcel;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button sButton;
    private TextInputEditText isbnCode;
    private String isbn;

    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sButton = findViewById(R.id.searchButton);
        scanButton = findViewById(R.id.scanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewActivity(view);
            }
        });
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isbnCode = findViewById(R.id.isbnCodebox);
                isbn = isbnCode.getText().toString();
            }
        });
    }

    public void previewActivity(View view){
        Intent intent = new Intent(this,PreviewActivity.class);
        startActivity(intent);
    }


}



