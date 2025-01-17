package com.android.onlineshoppingapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlineshoppingapp.adapters.RecyclerViewAdapterProduct;
import com.android.onlineshoppingapp.models.Product;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MyStoreActivity extends AppCompatActivity {

    private RecyclerView rvPopularProducts, rvRecentlyProducts;
    private RecyclerViewAdapterProduct recyclerViewAdapterProduct;
    private List<Product> popularProductList, recentlyProductList;

    private TextView tvShopName;
    private CardView cardAddProduct, cardManageProduct;
    private ImageView ivVerifyBtnStore, ivBackToProfile, ivAvatarStore;
    private CardView cardLogout, cardChangePass, cardDeleteAccount, cardPolicy, cardVersion;
    private Button btnAddImage, btnAddProduct;

    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    public static List<String> imageUriList = new ArrayList<String>();

    private List<Uri> imageList = new ArrayList<Uri>();
    private String newProductId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_store);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // init
        tvShopName = findViewById(R.id.tvShopName);
        cardAddProduct = findViewById(R.id.cardAddProduct);
        cardManageProduct = findViewById(R.id.cardManageProduct);
        ivVerifyBtnStore = findViewById(R.id.ivVerifyBtnStore);
        ivBackToProfile = findViewById(R.id.ivBackToProfile);
        ivAvatarStore = findViewById(R.id.ivAvatarStore);




        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(ivAvatarStore);
        }
        // click on back
        ivBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // change shop name
        String fullname = MainActivity.userInformation.getLastName() + " " + MainActivity.userInformation.getFirstName();
        tvShopName.setText(fullname);

        // click on verify button store
        verifyStoreBtn();

        // show product
        showPopularProduct();
        showRecentlyProduct();

        // click on card add product
        cardAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductBottomSheetView();
            }
        });

        // test
        cardManageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    // --------------- Function -----------------

    private void addProductBottomSheetView() {

        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_addproduct, null);
        BottomSheetDialog bottomSheetDialogAddProduct = new BottomSheetDialog(MyStoreActivity.this);
        bottomSheetDialogAddProduct.setContentView(sheetView);
        bottomSheetDialogAddProduct.setCancelable(false);
        bottomSheetDialogAddProduct.show();
        btnAddProduct = sheetView.findViewById(R.id.btnBottomSheetAddProduct);
        btnAddImage = sheetView.findViewById(R.id.btnAddImage);


        // click on close bottom sheet
        sheetView.findViewById(R.id.bottomSheetAddProductClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close bottom sheet dialog
                bottomSheetDialogAddProduct.dismiss();
            }
        });

        // check product name
        TextInputEditText etProductName = sheetView.findViewById(R.id.bottomSheetAddProductName);
        TextInputLayout layoutProductName = sheetView.findViewById(R.id.layout_bottomSheetAddProductName);
        etProductName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (etProductName.getText().toString().equals("")) {
                        layoutProductName.setHelperText("Tên sản phẩm không được để trống");
                    }
                } else {
                    layoutProductName.setHelperTextEnabled(false);
                }
            }
        });

        // check product description
        TextInputEditText etProductDescription = sheetView.findViewById(R.id.bottomSheetAddProductDescription);
        TextInputLayout layoutProductDescription = sheetView.findViewById(R.id.layout_bottomSheetAddProductDescription);
        etProductDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (etProductDescription.getText().toString().equals("")) {
                        layoutProductDescription.setHelperText("Mô tả sản phẩm không được để trống");
                    }
                } else {
                    layoutProductDescription.setHelperTextEnabled(false);
                }
            }
        });

        // check product price
        TextInputEditText etProductPrice = sheetView.findViewById(R.id.bottomSheetAddProductPrice);
        TextInputLayout layoutProductPrice = sheetView.findViewById(R.id.layout_bottomSheetAddProductPrice);
        etProductPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (etProductPrice.getText().toString().equals("")) {
                        layoutProductPrice.setHelperText("Giá sản phẩm không được để trống");
                    }
                } else {
                    layoutProductPrice.setHelperTextEnabled(false);
                }
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, 1);
            }

        });

        // click on add button
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newProductId = db.collection("Products").document().getId();
                uploadImage();
                addProduct(etProductName.getText().toString(), etProductDescription.getText().toString(), Integer.parseInt(etProductPrice.getText().toString()));
                bottomSheetDialogAddProduct.dismiss();
            }
        });

    }

    private void addProduct(String etProductName, String etProductDescription, int etProductPrice) {
        Map<String, Object> product = new HashMap<>();
        product.put("productName", etProductName);
        product.put("seller", fAuth.getCurrentUser().getUid());
        product.put("description", etProductDescription);
        product.put("productPrice", etProductPrice);
        product.put("rate", 0);
        product.put("likeNumber", 0);
        product.put("quantitySold", 0);
        db.collection("Products").document(newProductId).set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                imageUriList.clear();
                imageList.clear();
                Toast.makeText(MyStoreActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void verifyStoreBtn() {
        ivVerifyBtnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyStoreActivity.this);
                alertDialog.setTitle("Xác minh cửa hàng")
                        .setMessage("Bạn cần tối thiểu 100.000 người theo dõi để được chuyển đổi sang trạng thái \nĐÃ XÁC MINH")
                        .setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void showRecentlyProduct() {

        recentlyProductList = new ArrayList<>();
        rvRecentlyProducts = findViewById(R.id.rvRecentlyProducts);

        db.collection("Products")
                .whereEqualTo("seller", fAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                product.setProductId(document.getId());
                                recentlyProductList.add(product);
                            }

                            // setup recyclerview: recently products
                            recyclerViewAdapterProduct = new RecyclerViewAdapterProduct(recentlyProductList, MyStoreActivity.this);
                            rvRecentlyProducts.setLayoutManager(new LinearLayoutManager(MyStoreActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            rvRecentlyProducts.setAdapter(recyclerViewAdapterProduct);
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void showPopularProduct() {

        popularProductList = new ArrayList<>();
        rvPopularProducts = findViewById(R.id.rvPopularProducts);

        db.collection("Products")
                .whereEqualTo("seller", fAuth.getCurrentUser().getUid())
                .orderBy("quantitySold", Query.Direction.DESCENDING)
                .orderBy("productPrice", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                product.setProductId(document.getId());
                                popularProductList.add(product);
                            }

                            // setup recyclerview: recently products
                            recyclerViewAdapterProduct = new RecyclerViewAdapterProduct(popularProductList, MyStoreActivity.this);
                            rvPopularProducts.setLayoutManager(new LinearLayoutManager(MyStoreActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            rvPopularProducts.setAdapter(recyclerViewAdapterProduct);
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        imageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }
//                    textView.setVisibility(View.VISIBLE);
//                    textView.setText("You Have Selected "+ ImageList.size() +" Pictures" );
//                    choose.setVisibility(View.GONE);

                }

            }

        }

    }


    private void uploadImage() {
        for (int i = 0; i < imageList.size(); i++) {
            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference()
                    .child("productImages")
                    .child(newProductId).child(String.valueOf(i));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageList.get(i));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                ref.putBytes(byteArrayOutputStream.toByteArray())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                getDownloadUri(ref);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MyStoreActivity.this, "Có lỗi xảy ra " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, e.getMessage());
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, String.valueOf(imageUriList.size()));


    }

    private void getDownloadUri(StorageReference ref) {
        ref.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    imageUriList.add(String.valueOf(uri));
                    Map<String, Object> image = new HashMap<>();
                    image.put("url",imageUriList);
                    db.collection("productImages").document(newProductId).set(image);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Có lỗi xảy ra " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

}