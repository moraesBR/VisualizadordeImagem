package senac.visualizadordeimagem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import senac.visualizadordeimagem.adapters.PhotoAdapter;
import senac.visualizadordeimagem.models.Photo;

public class MainActivity extends AppCompatActivity {

    /* ------------------------- Variáveis -------------------------- */
    /* Contador de fotos para deletar do RecyclerView */
    private int numPhotosForDelete = 0;
    /* Armazena a configuração prévia do layout do RecycleView */
    private Parcelable savedRecyclerLayout;

    /* Models */
    private Photo mainPhoto;
    private List<Photo> listPhotos;

    /* RecyclerView, LayoutManager e Adapter */
    private PhotoAdapter photoAdapter;
    private RecyclerView rvPhotos;
    private RecyclerView.LayoutManager lmPhotos;

    /* Buttons */
    private ImageButton btnNewPhoto;
    private ImageButton btnAddPhoto;
    private ImageButton btnDeletePhoto;

    /* Clicks Listener */
    private View.OnLongClickListener checkedPhotos;
    private View.OnClickListener selectedPhoto;

    /* Itens do layout activity */
    private LinearLayout linlay1;
    private TextView tvImgSelec;
    private ImageView ivMainPhoto;

    /* ------------------------- Identificadores -------------------------- */
    /* Solicitação de inicialização de atividade */
    private static final int PICK_IMAGE_REQUEST = 1;
    /* Referência ao conteúdo do Array List de Photos */
    private static final String LIST_STATE = "list_state";
    /* Referência à foto principal do layout */
    private static final String MAIN_PHOTO = "main_photo";
    /* Referência à configuração de layout do RecyclerView */
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

/* ---------------------------------- Ciclo de Vida do App -------------------------------------- */
    /*
     *  Métodos
     *      onCreate():
     *      onSaveInstanceState():
     *      onRestoreInstanceState():
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBindView();
        setAdapter(savedInstanceState);
        clickable();
    }

    /* ---------------------- SALVAR CONTEXTO ---------------------- */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_STATE, (ArrayList<? extends Parcelable>) listPhotos);
        outState.putParcelable(MAIN_PHOTO,mainPhoto);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT,rvPhotos.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle inState){
        super.onRestoreInstanceState(inState);
        listPhotos  = inState.getParcelableArrayList(LIST_STATE);
        mainPhoto   = inState.getParcelable(MAIN_PHOTO);
        savedRecyclerLayout    = inState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
    }

/* ---------------------------------- Métodos Data Binding -------------------------------------- */
    /*
     *  Métodos
     *      dataBindView(): Realiza a ligação dos elementos do layout com variáveis programáveis.
     *      setAdapter(): Configura os dados e o layout do RecyclerView de fotos.
     */


    private void dataBindView(){
        ivMainPhoto    = findViewById(R.id.visualizador_iv_photo);
        rvPhotos       = findViewById(R.id.visualizador_recycler);
        btnNewPhoto    = findViewById(R.id.btn_new_photo);
        btnAddPhoto    = findViewById(R.id.btn_add_photo);
        btnDeletePhoto = findViewById(R.id.btn_delete_photo);
        linlay1        = findViewById(R.id.visualizador_linlay1);
        tvImgSelec     = findViewById(R.id.tv_img_selec);
    }

    private void setAdapter(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            listPhotos = savedInstanceState.getParcelableArrayList(LIST_STATE);
            mainPhoto  = savedInstanceState.getParcelable(MAIN_PHOTO);
        }else{
            listPhotos = new ArrayList<>();
            mainPhoto = null;
        }

        photoAdapter = new PhotoAdapter(listPhotos, getBaseContext());
        lmPhotos = new LinearLayoutManager(getBaseContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(lmPhotos);
        rvPhotos.setHasFixedSize(true);
        rvPhotos.setAdapter(photoAdapter);

        if (savedRecyclerLayout != null){
            rvPhotos.getLayoutManager().onRestoreInstanceState(savedRecyclerLayout);
        }

        setPhotosView();
    }

/* ---------------------------------- Métodos de Controles -------------------------------------- */
    /*
     *  Métodos
     *      setPhotosView(): controla a visibilidade dos elementos responsáveis pela apresentação
     *          das imagens.
     *      clickable(): Controla as ações de cliques dos botões e afins
     */


    /*  setPhotosView(): controla a visibilidade dos elementos responsáveis pela apresentação das
     *      imagens. Se não houver fotos no adaptador de fotos, então tais elementos são escondidos;
     *      caso contrário, serão apresentados.
     */
    private void setPhotosView(){
        if(listPhotos.isEmpty()) {
            mainPhoto = null;
            ivMainPhoto.setVisibility(View.GONE);
            linlay1.setVisibility(View.GONE);
            tvImgSelec.setVisibility(View.VISIBLE);
        }else{
            ivMainPhoto.setVisibility(View.VISIBLE);
            linlay1.setVisibility(View.VISIBLE);
            tvImgSelec.setVisibility(View.GONE);
            if (listPhotos.size() == 1)
                mainPhoto = listPhotos.get(0);
            Picasso.get().load(mainPhoto.getImage())
                    .resize(360, 200)
                    .into(ivMainPhoto);
        }
    }


    /* clickable(): Controla as ações de cliques dos botões e afins */
    private void clickable() {
        /* ---------------------- Clicks no RecyclerView ---------------------- */
        /* Marca fotos para a exclusão */
        checkedPhotos = new View.OnLongClickListener() {
            @Override
            /* Após o click longo na imagem dentro do RecyclerView */
            public boolean onLongClick(View view) {
                /* view.getTag(): captura o viewholder clicado atrelado ao RecyclerView */
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();

                /* Captura a posição correspondente no adapter */
                int position = viewHolder.getAdapterPosition();

                /*
                 * Se não estiver sido selecionado, então adicione no contador de imagens
                 * selecionadas; senão, retire do contador.
                 */
                if(!listPhotos.get(position).isSelected()){
                   numPhotosForDelete++;
                }else
                   numPhotosForDelete--;

                /* Altera o estado de marcação (boolean) */
                listPhotos.get(position).setSelect();

                /* Informa ao adapter que houve ateração nos dados */
                photoAdapter.notifyDataSetChanged();

                /* Determina se o botão de excluir fotos será apresentado ou não no layout.
                 * Se a quantidade de fotos no contador for maior que 0, então apresente-o;
                 * Senão, esconda-o*/
                if(numPhotosForDelete > 0){
                    btnDeletePhoto.setVisibility(View.VISIBLE);
                    return true;
                }
                else {
                    btnDeletePhoto.setVisibility(View.GONE);
                    return false;
                }
            }
        };
        photoAdapter.setCheckedItem(checkedPhotos);

        /* Seleciona a foto no RecyclerView para apresentar no layout de imagem principal */
        selectedPhoto = new View.OnClickListener() {
            @Override
            /* Após o click curto na imagem dentro do RecyclerView */
            public void onClick(View view) {
                /* view.getTag(): captura o viewholder clicado atrelado ao RecyclerView */
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();

                /* Captura a posição correspondente no adapter */
                int position = viewHolder.getAdapterPosition();

                /* Atualiza a foto principal */
                mainPhoto = listPhotos.get(position);

                /* Apresenta a foto no layout main */
                Picasso.get().load(mainPhoto.getImage())
                        .resize(360,200)
                        .into(ivMainPhoto);
            }
        };
        photoAdapter.setSelectedItem(selectedPhoto);

        /* Adiciona uma foto ao RecyclerView via câmera */
        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* ---------------------- Clicks em Buttons ---------------------- */
        /* Adciona fotos ao RecyclerView via External Storage */
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        /* Deleta as fotos selecionadas no RecyclerView */
        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Apaga as fotos se houver pelo menos uma foto selecionada */
                if(numPhotosForDelete > 0){
                    /* Percorre o ArrayList de Photos */
                    for(int i=0; i < listPhotos.size(); i++){
                        /*
                         *  Se a foto estiver selecionada, então remova-a e determine qual foto será
                         *  a foto principal
                         */
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

                    /* Zera o contado de fotos selecionadas */
                    numPhotosForDelete = 0;

                    /* notifica o adapter sobre a modificação no List associado a ele */
                    photoAdapter.notifyDataSetChanged();

                    /* esconde o botão de exclusão de fotos */
                    btnDeletePhoto.setVisibility(View.GONE);

                    /* Determina se será apresentado os layouts de foto e RecycleView */
                    setPhotosView();
                }
            }
        });
    }


/* ---------------------------------- Métodos auxiliares ---------------------------------------- */
    /*
     *  Métodos
     *      openFileChooser(): Busca uma imagem através de uma activity padrão
     *      onActivityResult():  Recebe e trata o resultado obtido pelo método openFileChooser.
     */

    /* ----------------- Gerar resultados a partir de uma activity ----------------- */
    /*
     *  openFileChooser(): Busca uma imagem através de uma activity padrão e retorna o resultado via
     *      identificado (PICK_IMAGE_REQUEST). Quando o usuário terminar esta atividade, o sistema
     *      chamará o método onActivityResult()
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    /*
     *  onActivityResult(): Recebe e trata o resultado obtido pelo método openFileChooser. Possui
     *      três argumentos. O requestCode indica o código de solicitação passado ao método
     *      startActivityForResult(); resultCode informa o código de resultado pela atividade gerada
     *      em openFileChooser; data que é o Intent com os dados coletados, que no caso é aquela um
     *      arquivo de foto qualquer.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*  Se o código de requisição for PICK_IMAGE_REQUEST, resultado ok, houve dados não nulos,
         *      então, adicione uma foto ao RecyclerView.
         */
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            Photo photo = new Photo(data.getData());
            listPhotos.add(photo);
            photoAdapter.notifyDataSetChanged();

            /* Determina se será apresentado os layouts de foto e RecycleView */
            setPhotosView();
        }
    }
}
