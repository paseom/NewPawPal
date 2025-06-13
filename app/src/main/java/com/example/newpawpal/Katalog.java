package com.example.newpawpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Katalog extends AppCompatActivity implements ProductAdapter.OnItemClickListener, CategoryAdapter.OnCategoryClickListener {

    private static final String TAG = "KatalogActivity";

    private EditText etSearch;
    private ImageView cartIcon;
    private ImageView notificationIcon;
    private RecyclerView rvCategories;
    private RecyclerView rvProducts;
    private BottomNavigationView bottomNavigationView;

    private ProductAdapter productAdapter;
    private List<Product> productList;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    private FirebaseDatabase database;
    private DatabaseReference productsRef;
    private Map<String, Integer> cartQuantities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);
        Log.d(TAG, "=== KATALOG ACTIVITY STARTED ===");

        try {
            setContentView(R.layout.activity_katalog);
            Log.d(TAG, "Layout set successfully");

            // Inisialisasi views
            etSearch = findViewById(R.id.etSearch);
            Log.d(TAG, "etSearch initialized");

            rvCategories = findViewById(R.id.rvCategories);
            Log.d(TAG, "rvCategories initialized");

            rvProducts = findViewById(R.id.rvProducts);
            Log.d(TAG, "rvProducts initialized");

            // ... seterusnya dengan log di setiap step

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
        }

        etSearch = findViewById(R.id.etSearch);
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        cartIcon = findViewById(R.id.iconCart);
        notificationIcon = findViewById(R.id.iconNotification);

        database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products"); // Pastikan ini 'products'

        cartQuantities = new HashMap<>();

        categoryList = new ArrayList<>();
        categoryList.add(new Category(getString(R.string.category_all), true));
        categoryList.add(new Category(getString(R.string.category_body_care), false));
        categoryList.add(new Category(getString(R.string.category_accessories), false));
        categoryList.add(new Category("Makanan", false));
        categoryList.add(new Category("Mainan", false));

        categoryAdapter = new CategoryAdapter(categoryList, this);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);

        fetchProductsFromRealtimeDatabase();

        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Katalog.this, Keranjang.class);
            startActivity(intent);
        });

        notificationIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Katalog.this, Notifikasi.class);
            startActivity(intent);
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }



    private void fetchProductsFromRealtimeDatabase() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setId(snapshot.getKey());
                        productList.add(product);
                    } else {
                        // Log jika ada masalah konversi
                        Log.e(TAG, "Failed to parse product from snapshot: " + snapshot.getKey());
                    }
                }
                productAdapter.setProducts(productList);
                Log.d(TAG, "Products fetched successfully. Count: " + productList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                Toast.makeText(Katalog.this, "Gagal mengambil data produk: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddToCartClick(Product product) {
        int currentQuantity = cartQuantities.getOrDefault(product.getId(), 0);
        if (currentQuantity == 0) {
            cartQuantities.put(product.getId(), 1);
            productAdapter.updateProductQuantity(product.getId(), 1);
            Toast.makeText(this, "Ditambahkan ke keranjang: " + product.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddQuantityClick(Product product) {
        int currentQuantity = cartQuantities.getOrDefault(product.getId(), 0);
        if (currentQuantity < product.getStock()) {
            int newQuantity = currentQuantity + 1;
            cartQuantities.put(product.getId(), newQuantity);
            productAdapter.updateProductQuantity(product.getId(), newQuantity);
            Toast.makeText(this, "Kuantitas " + product.getName() + " bertambah: " + newQuantity, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Stok " + product.getName() + " habis!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveQuantityClick(Product product) {
        int currentQuantity = cartQuantities.getOrDefault(product.getId(), 0) ;
        if (currentQuantity > 0) {
            int newQuantity = currentQuantity - 1;
            cartQuantities.put(product.getId(), newQuantity);
            productAdapter.updateProductQuantity(product.getId(), newQuantity);
            Toast.makeText(this, "Kuantitas " + product.getName() + " berkurang: " + newQuantity, Toast.LENGTH_SHORT).show();
        }
        if (currentQuantity - 1 == 0) {
            cartQuantities.remove(product.getId());
        }
    }

    @Override
    public void onItemClick(Product product) {
        if (product != null && product.getId() != null) {
            Intent detailIntent = new Intent(Katalog.this, DetailProduk.class);
            detailIntent.putExtra("product_id", product.getId()); // Kirim ID produk
            startActivity(detailIntent);
        } else {
            Log.e(TAG, "onItemClick: Product or Product ID is null.");
            Toast.makeText(this, "Tidak dapat membuka detail produk (ID tidak ditemukan).", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCategoryClick(Category category, int position) {
        Toast.makeText(this, "Kategori dipilih: " + category.getName(), Toast.LENGTH_SHORT).show();
    }
}
