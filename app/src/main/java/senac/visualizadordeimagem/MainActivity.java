package senac.visualizadordeimagem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import senac.visualizadordeimagem.adapters.PhotoAdapter;
import senac.visualizadordeimagem.models.Photo;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private int selected = 0;
    private Photo mainPhoto;
    private List<Photo> listPhotos = new ArrayList<>();
    private PhotoAdapter photoAdapter;
    private ImageView ivMainPhoto;
    private RecyclerView rvPhotos;
    private RecyclerView.LayoutManager lmPhotos;
    private ImageButton btnNewPhoto;
    private ImageButton btnAddPhoto;
    private ImageButton btnDeletePhoto;
    private LinearLayout linlay1;
    private TextView tvImgSelec;
    /*private FloatingActionButton addPhoto;
    private FloatingActionButton removePhoto;*/
    private View.OnLongClickListener checkedPhoto;
    private View.OnClickListener selectedPhoto;
    private Uri mImageUri;
    private ProgressBar pbLoandig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBindView();
        setAdapter();
        clickable();
    }

    private void dataBindView(){
        ivMainPhoto    = findViewById(R.id.visualizador_iv_photo);
        rvPhotos       = findViewById(R.id.visualizador_recycler);
        btnNewPhoto    = findViewById(R.id.btn_new_photo);
        btnAddPhoto    = findViewById(R.id.btn_add_photo);
        btnDeletePhoto = findViewById(R.id.btn_delete_photo);
        linlay1        = findViewById(R.id.visualizador_linlay1);
        tvImgSelec     = findViewById(R.id.tv_img_selec);
    }

    private void setAdapter() {
        photoAdapter = new PhotoAdapter(listPhotos,getBaseContext());
        lmPhotos     = new LinearLayoutManager(getBaseContext(),
                LinearLayoutManager.HORIZONTAL,false);
        rvPhotos.setLayoutManager(lmPhotos);
        rvPhotos.setHasFixedSize(true);
        rvPhotos.setAdapter(photoAdapter);
    }

    private void clickable() {
        checkedPhoto = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                if(!listPhotos.get(position).isSelected()){
                   selected++;
                }else
                   selected--;

                listPhotos.get(position).setSelect();
                photoAdapter.notifyDataSetChanged();

                if(selected > 0){
                    btnDeletePhoto.setVisibility(View.VISIBLE);
                    return true;
                }
                else {
                    btnDeletePhoto.setVisibility(View.GONE);
                    return false;
                }
            }
        };
        photoAdapter.setCheckedItem(checkedPhoto);

        selectedPhoto = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                mainPhoto = listPhotos.get(position);
                Picasso.get().load(mainPhoto.getImage()).resize(360,200).into(ivMainPhoto);
            }
        };
        photoAdapter.setSelectedItem(selectedPhoto);

        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected > 0){
                    for(int i=0; i < listPhotos.size(); i++){
                        if(listPhotos.get(i).isSelected()) {
                            if(listPhotos.get(i).equals(mainPhoto)) {
                                    listPhotos.remove(i);
                                    if(!listPhotos.isEmpty())
                                        mainPhoto = listPhotos.get(0);
                            }else
                                listPhotos.remove(i);
                            i--;
                        }
                    }

                    selected = 0;
                    photoAdapter.notifyDataSetChanged();
                    btnDeletePhoto.setVisibility(View.GONE);

                    if(!listPhotos.isEmpty()){
                        Picasso.get().load(mainPhoto.getImage()).resize(360, 200).into(ivMainPhoto);
                    }else{
                        mainPhoto = null;
                        ivMainPhoto.setVisibility(View.GONE);
                        linlay1.setVisibility(View.GONE);
                        tvImgSelec.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            Photo photo = new Photo(data.getData());
            listPhotos.add(photo);
            photoAdapter.notifyDataSetChanged();
            if(listPhotos.size() > 0) {
                ivMainPhoto.setVisibility(View.VISIBLE);
                linlay1.setVisibility(View.VISIBLE);
                tvImgSelec.setVisibility(View.GONE);

                if (listPhotos.size() == 1) {
                    mainPhoto = listPhotos.get(0);
                    Picasso.get().load(mainPhoto.getImage()).resize(360, 200).into(ivMainPhoto);
                }
            }
        }
    }

    private static final String SCREEN_TEXT_KEY = "TELA VISUALIZADOR";


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable(SCREEN_TEXT_KEY,lmPhotos.onSaveInstanceState());
        outState.putParcelable(SCREEN_TEXT_KEY,mainPhoto);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle inState){
        super.onRestoreInstanceState(inState);
        lmPhotos  = inState.getParcelable(SCREEN_TEXT_KEY);
        mainPhoto = inState.getParcelable(SCREEN_TEXT_KEY);
    }


}
