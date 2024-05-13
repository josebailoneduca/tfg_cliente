package josebailon.ensayos.cliente.view.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.databinding.NotaItemBinding;
import josebailon.ensayos.cliente.view.fragment.VercanciondetalleFragment;


public class NotasConAudioAdapter extends RecyclerView.Adapter<NotasConAudioAdapter.ViewHolder> {

    List<NotaAndAudio> list;
    VercanciondetalleFragment fragment;

    public NotasConAudioAdapter(List<NotaAndAudio> list, VercanciondetalleFragment fragment) {
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotaItemBinding binding =NotaItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotaAndAudio item = list.get(position);
        holder.binding.nombre.setText(item.nota.getNombre());
        holder.binding.imgTexto.setVisibility((TextUtils.isEmpty(item.nota.getTexto().toString()))?View.INVISIBLE : View.VISIBLE);
        holder.binding.imgAudio.setVisibility((item.audio==null)?View.INVISIBLE : View.VISIBLE);
        holder.itemView.setOnClickListener(v -> fragment.verNota(item.nota.getId()));
        holder.itemView.setOnLongClickListener(v -> fragment.mostrarMenuNota(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<NotaAndAudio> notas) {
        this.list=notas;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        NotaItemBinding binding;
        public ViewHolder(@NonNull NotaItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

    }
}