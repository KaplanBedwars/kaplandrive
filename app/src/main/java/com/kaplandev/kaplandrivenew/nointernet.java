package com.kaplandev.kaplandrivenew;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class nointernet extends AppCompatActivity {

    private ViewPager photosViewPager;
    private LinearLayout dotsLayout;
    private int[] photos = {
            R.drawable.troll1,
            R.drawable.troll2,
            R.drawable.troll3,
            R.drawable.troll4,
            R.drawable.troll5,
            R.drawable.troll6,
            R.drawable.troll7,
            R.drawable.troll8,
            R.drawable.troll9,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet_page);
        DisplayHelper.hideCameraCutout(this);

        if (!superman.isnoEnabled(this)) {

           finish();
        }

        photosViewPager = findViewById(R.id.photosViewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        PhotosAdapter adapter = new PhotosAdapter(photos);
        photosViewPager.setAdapter(adapter);

        setupDotsIndicator();

        findViewById(R.id.btnRetry).setOnClickListener(v -> checkConnection());
        findViewById(R.id.btnSettings).setOnClickListener(v -> openSettings());
    }

    private void setupDotsIndicator() {
        ImageView[] dots = new ImageView[photos.length];
        dotsLayout.removeAllViews(); // Önceki noktaları temizle

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this,
                    i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }

        photosViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dots.length; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(nointernet.this,
                            i == position ? R.drawable.dot_active : R.drawable.dot_inactive));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void checkConnection() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        // Buraya gerçek bağlantı kontrol kodu eklenebilir.
    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }

    // ViewPager Adapter
    private static class PhotosAdapter extends PagerAdapter {
        private final int[] photos;

        public PhotosAdapter(int[] photos) {
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return photos.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setImageResource(photos[position]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
