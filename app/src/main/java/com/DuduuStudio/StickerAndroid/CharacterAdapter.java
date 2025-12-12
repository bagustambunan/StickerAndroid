package com.DuduuStudio.StickerAndroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {
    
    private List<Character> characters;
    private OnCharacterClickListener listener;
    private Context context;
    private int selectedPosition = -1; // -1 means no selection (show all)

    public interface OnCharacterClickListener {
        void onCharacterClick(Character character, int position);
        void onAllCharactersClick(); // For "All" option
    }

    public CharacterAdapter(Context context, List<Character> characters) {
        this.context = context;
        this.characters = characters != null ? characters : new ArrayList<>();
    }

    public void setOnCharacterClickListener(OnCharacterClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characters.get(position);
        holder.bind(character, position);
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    public void updateCharacters(List<Character> newCharacters) {
        this.characters = newCharacters != null ? newCharacters : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedCharacter(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    public Character getSelectedCharacter() {
        if (selectedPosition >= 0 && selectedPosition < characters.size()) {
            return characters.get(selectedPosition);
        }
        return null;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    class CharacterViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView characterImage;
        private View frameLayout;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);
            characterImage = itemView.findViewById(R.id.character_image);
            frameLayout = itemView.findViewById(R.id.character_frame);
        }

        public void bind(Character character, int position) {
            // Load image from assets using Bitmap
            try {
                InputStream inputStream = context.getAssets().open(character.getImage());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                characterImage.setImageBitmap(bitmap);
                inputStream.close();
                android.util.Log.d("CharacterAdapter", "Loaded image: " + character.getImage());
            } catch (IOException e) {
                e.printStackTrace();
                android.util.Log.e("CharacterAdapter", "Failed to load image: " + character.getImage(), e);
                // Set a placeholder or default image
                characterImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Set selection state using drawable selector for the frame layout
            frameLayout.setSelected(position == selectedPosition);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    if (position == selectedPosition) {
                        // If clicking on already selected character, clear selection
                        clearSelection();
                        listener.onAllCharactersClick();
                    } else {
                        // Select new character
                        setSelectedCharacter(position);
                        listener.onCharacterClick(character, position);
                    }
                }
            });
        }
    }
} 