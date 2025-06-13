package com.example.newpawpal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.HashMap; // Untuk menyimpan kuantitas di keranjang
import java.util.Map; // Untuk menyimpan kuantitas di keranjang

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnItemClickListener listener;
    // Peta untuk menyimpan kuantitas setiap produk yang ditambahkan ke keranjang
    private Map<String, Integer> productQuantities;

    public interface OnItemClickListener {
        void onAddToCartClick(Product product); // Untuk klik ikon keranjang (add to cart awal)
        void onItemClick(Product product); // Untuk klik keseluruhan item
        void onAddQuantityClick(Product product); // Untuk menambah kuantitas
        void onRemoveQuantityClick(Product product); // Untuk mengurangi kuantitas
    }

    public ProductAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
        this.productQuantities = new HashMap<>(); // Inisialisasi peta kuantitas
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        // Pastikan ID produk tidak null sebelum digunakan sebagai kunci peta
        int currentQuantity = product.getId() != null ? productQuantities.getOrDefault(product.getId(), 0) : 0;
        holder.bind(product, listener, currentQuantity); // Kirim kuantitas ke bind
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged(); // Memberi tahu RecyclerView bahwa datanya telah berubah
    }

    /**
     * Memperbarui kuantitas produk tertentu dalam adapter dan memberi tahu RecyclerView untuk memperbarui tampilan item.
     * @param productId ID unik produk.
     * @param quantity Kuantitas baru produk.
     */
    public void updateProductQuantity(String productId, int quantity) {
        if (productId != null) {
            productQuantities.put(productId, quantity);
            // Temukan posisi produk di list dan update item tersebut agar RecyclerView diperbarui
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId() != null && productList.get(i).getId().equals(productId)) {
                    notifyItemChanged(i);
                    return; // Keluar setelah menemukan dan memperbarui
                }
            }
        }
    }


    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        ImageView ivAddToCart;
        TextView tvProductName;
        TextView tvProductRating;
        TextView tvProductPrice;
        // Penambahan elemen UI baru
        LinearLayout quantityControlLayout;
        ImageView ivRemoveQuantity;
        TextView tvQuantity;
        ImageView ivAddQuantity;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivAddToCart = itemView.findViewById(R.id.ivAddToCart);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductRating = itemView.findViewById(R.id.tvProductRating);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }

        void bind(final Product product, final OnItemClickListener listener, int currentQuantity) {
            // Memuat gambar menggunakan Glide
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_image) // Gambar placeholder
                    .error(R.drawable.placeholder_image) // Gambar jika terjadi error
                    .into(ivProductImage);

            tvProductName.setText(product.getName());

            // Cek apakah rating ada, jika tidak, sembunyikan atau set default
            // Asumsi rating 0.0 berarti tidak ada atau tidak valid
            if (product.getRating() > 0) {
                tvProductRating.setText(String.format(Locale.getDefault(), "%.1f", product.getRating()));
                tvProductRating.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.ratingLayout).setVisibility(View.VISIBLE);
            } else {
                tvProductRating.setVisibility(View.GONE);
                itemView.findViewById(R.id.ratingLayout).setVisibility(View.GONE);
            }


            // Format harga ke Rupiah
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            tvProductPrice.setText(formatRupiah.format(product.getPrice()));

            // Logika untuk menampilkan/menyembunyikan tombol quantity control
            if (currentQuantity > 0) {
                ivAddToCart.setVisibility(View.GONE); // Sembunyikan ikon keranjang
                quantityControlLayout.setVisibility(View.VISIBLE); // Tampilkan kontrol kuantitas
                tvQuantity.setText(String.valueOf(currentQuantity));
                ivRemoveQuantity.setVisibility(View.VISIBLE);
                tvQuantity.setVisibility(View.VISIBLE);
                ivAddQuantity.setVisibility(View.VISIBLE);
            } else {
                ivAddToCart.setVisibility(View.VISIBLE); // Tampilkan ikon keranjang
                quantityControlLayout.setVisibility(View.GONE); // Sembunyikan kontrol kuantitas
            }

            // Set OnClickListener untuk ikon keranjang (add to cart awal)
            ivAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product); // Panggil metode add to cart
                }
            });

            // Set OnClickListener untuk tombol tambah kuantitas
            ivAddQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddQuantityClick(product);
                }
            });

            // Set OnClickListener untuk tombol kurang kuantitas
            ivRemoveQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveQuantityClick(product);
                }
            });

            // Set OnClickListener untuk keseluruhan item (untuk detail produk)
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(product);
                }
            });
        }
    }
}
