package senac.visualizadordeimagem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import senac.visualizadordeimagem.R;
import senac.visualizadordeimagem.models.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Photo> photos;
    private Context context;
    private View.OnClickListener selectedItem;
    private View.OnLongClickListener checkedItem;

    public PhotoAdapter(List<Photo> photos, Context context) {
        this.photos = photos;
        this.context = context;
    }

    public void setSelectedItem(View.OnClickListener selectItem) {
        this.selectedItem = selectItem;
    }

    public void setCheckedItem(View.OnLongClickListener checkedItem){
        this.checkedItem = checkedItem;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item,parent,false);
        PhotoViewHolder holder = new PhotoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        if(photos.get(position).isSelected())
            holder.image.setBackgroundResource(R.color.colorError);
        else
            holder.image.setBackgroundResource(R.color.transparent);
        Picasso.get().load(photos.get(position).getImage()).resize(80,80).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_item_view);
            itemView.setTag(this);
            itemView.setOnClickListener(selectedItem);
            itemView.setOnLongClickListener(checkedItem);
        }
    }
}
