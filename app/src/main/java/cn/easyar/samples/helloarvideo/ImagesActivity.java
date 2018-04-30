package cn.easyar.samples.helloarvideo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.io.File;
import java.nio.file.FileSystem;
import java.util.List;
import java.util.zip.Inflater;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImagesActivity extends AppCompatActivity {
    private User mUser = null;
    private UserService mUserService = null;
    private List<ImageInfo> mImageInfos;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mImageRecyclerView;
    private ImageListItemAdapter mAdapter;
    AppCompatButton mdownloadButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.layout_swipe_refresh_images);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            public void onRefresh() {
                //loadImages();
                Toast.makeText(ImagesActivity.this, "刷新图片完成", Toast.LENGTH_SHORT ).show();
                //数据重新加载完成后，提示数据发生改变，并且设置现在不在刷新
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        });

        mImageRecyclerView = (RecyclerView) findViewById(R.id.image_list);
        mImageRecyclerView.setLayoutManager(new GridLayoutManager(ImagesActivity.this, 3)); //(new LinearLayoutManager(ImagesActivity.this));
        mImageRecyclerView.setItemAnimator(null);//设置动画为null来解决闪烁问题

        // 下载按钮
        mdownloadButton = (AppCompatButton) findViewById(R.id.download_button);
        mdownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.moosao.cn:3000/users/"+mUser.getId()+"/app");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        loadLoginUser();
        loadImages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_images, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log_out:
                File file=new File(getFilesDir(), "login_data");
                if(file.exists()) {
                    file.delete();
                }

                Intent intent = new Intent(ImagesActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the login
        moveTaskToBack(true);
    }

    private void loadLoginUser() {
        try {
            Gson gson = new Gson();
            mUser = gson.fromJson(getIntent().getStringExtra("userInfo"), User.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mUser == null) {
                Intent intent = new Intent(ImagesActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private void loadImages() {
        // show progress bar
        final ProgressDialog progressDialog = new ProgressDialog(ImagesActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("加载图片中...");
        progressDialog.show();

        int user_id = mUser.getId();
        //Authorization:Token token=sbjDWGgs+rySmuxroxrcu/58rGwRCaDH4L5A/FkgElKMLJdfHLdnIT1/fCNynATrO64teClITGaxg2tytxpZlg==, email=user0@sample.com
        String token = "Token token=" + mUser.getToken() + ", email=" + mUser.getEmail();
        mUserService = ApiUtils.getUserService();
        mUserService.getImages(token, user_id).enqueue(new Callback<List<ImageInfo>>() {
            @Override
            public void onResponse(Call<List<ImageInfo>> call, final Response<List<ImageInfo>> response) {

                if(response.isSuccessful()) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    mImageInfos = response.body();
                                    updateUI();
                                    progressDialog.dismiss();
                                }
                            }, 0 );

                }else {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    Toast.makeText(ImagesActivity.this, "加载图片失败" , Toast.LENGTH_LONG ).show();
                                    progressDialog.dismiss();
                                }
                            }, 1000);
                }
            }

            @Override
            public void onFailure(Call<List<ImageInfo>> call, Throwable t) {
                Toast.makeText(ImagesActivity.this, "加载图片发生错误:" + t.getMessage(), Toast.LENGTH_LONG ).show();
                progressDialog.dismiss();
            }
        });
    }

    private void updateUI() {
        mAdapter = new ImageListItemAdapter(mImageInfos);
        mImageRecyclerView.setAdapter(mAdapter);
    }

    // 内部类
    private class ImageListItemHolder extends RecyclerView.ViewHolder {
        private ImageInfo mImageInfo;
        private ImageView mImage;
        //private TextView mImageTitle;
       // private TextView mImageSize;

        public ImageListItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.activity_image_item, parent, false));
            mImage = (ImageView) itemView.findViewById(R.id.image_thumb);
            //mImageTitle = (TextView) itemView.findViewById(R.id.image_title);
            //mImageSize = (TextView) itemView.findViewById(R.id.image_size);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ImagesActivity.this, mImageInfo.getThumbUrl() + " clicked image", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bind(ImageInfo imageInfo) {
            mImageInfo = imageInfo;

//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.drawable.placeholder);
//            requestOptions.error(R.drawable.placeholder);

//            Glide.with(ImagesActivity.this).setDefaultRequestOptions(requestOptions).load(mImageInfo.getThumbUrl()).into(mImage);
            Glide.with(ImagesActivity.this).load(mImageInfo.getThumbUrl()).into(mImage);
            //mImageTitle.setText(imageInfo.getName());
            //mImageSize.setText(imageInfo.getThumbUrl());
            //mImage.setImageResource(R.drawable.default_avatar);
        }
    }

    // 内部类
    private class ImageListItemAdapter extends RecyclerView.Adapter<ImageListItemHolder> {
        private List<ImageInfo> mImageInfos;

        public ImageListItemAdapter(List<ImageInfo> imageInfos) {
            mImageInfos = imageInfos;
        }

        @NonNull
        @Override
        public ImageListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ImagesActivity.this);
            return new ImageListItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageListItemHolder holder, int position) {
            ImageInfo imageInfo = mImageInfos.get(position);
            holder.bind(imageInfo);
        }

        @Override
        public int getItemCount() {
            return mImageInfos.size();
        }

        @Override
        public long getItemId(int position) {
            return mImageInfos.get(position).getId();
        }
    }
}
