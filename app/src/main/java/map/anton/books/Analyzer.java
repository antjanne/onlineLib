package map.anton.books;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.experimental.UseExperimental;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

class Analyzer implements ImageAnalysis.Analyzer {

    private List<String> isbns = new ArrayList<>();

    public List<String> getIsbns() {
        return isbns;
    }

    @Override
    public void analyze(ImageProxy imageProxy) {
        ByteBuffer ybuffer = imageProxy.getPlanes()[0].getBuffer();
        ByteBuffer ubuffer = imageProxy.getPlanes()[1].getBuffer();
        ByteBuffer vbuffer = imageProxy.getPlanes()[2].getBuffer();

        int ysize = ybuffer.remaining();
        int usize = ubuffer.remaining();
        int vsize = vbuffer.remaining();

        byte[] nv21 = new byte[ysize+usize+vsize];

        ybuffer.get(nv21,0,ysize);
        vbuffer.get(nv21,ysize,vsize);
        ubuffer.get(nv21,ysize+vsize,usize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21,imageProxy.getWidth(),imageProxy.getHeight(),null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0,0,yuvImage.getWidth(),yuvImage.getHeight()),75,out);
        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);

        InputImage image = InputImage.fromBitmap(bitmap,imageProxy.getImageInfo().getRotationDegrees());

            // Pass image to an ML Kit Vision API
            // ...
        barcodeScanner(image);

        imageProxy.close();
        }

        private void barcodeScanner(InputImage image) {

            BarcodeScannerOptions options =
                    new BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(
                                    Barcode.FORMAT_EAN_13,
                                    Barcode.FORMAT_EAN_8)
                            .build();
            BarcodeScanner barcodeScanner = BarcodeScanning.getClient(options);

            Task<List<Barcode>> result = barcodeScanner.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            for (Barcode barcode : barcodes) {
                                Rect bounds = barcode.getBoundingBox();
                                Point[] corners = barcode.getCornerPoints();

                                String rawValue = barcode.getRawValue();

                                int valueType = barcode.getValueType();
                                // See API reference for complete list of supported types
                                String isbn = barcode.getDisplayValue();
                                isbns.add(isbn);
                                break;
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }


}
