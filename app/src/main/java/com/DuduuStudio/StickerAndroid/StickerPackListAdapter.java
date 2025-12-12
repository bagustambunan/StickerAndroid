/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.DuduuStudio.StickerAndroid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StickerPackListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_STICKER_PACK = 0;
    private static final int VIEW_TYPE_INSTAGRAM_BANNER = 1;
    private static final int BANNER_INTERVAL = 10; // Show Instagram banner every 10 items
    
    private List<StickerPack> stickerPacks;

    StickerPackListAdapter(
        @NonNull List<StickerPack> stickerPacks
    ) {
        this.stickerPacks = stickerPacks;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_STICKER_PACK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int viewType) {
        final Context context = viewGroup.getContext();
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        
        if (viewType == VIEW_TYPE_INSTAGRAM_BANNER) {
            final View bannerView = layoutInflater.inflate(R.layout.instagram_banner_item, viewGroup, false);
            return new InstagramBannerViewHolder(bannerView);
        } else {
            final View stickerPackRow = layoutInflater.inflate(R.layout.sticker_packs_list_item, viewGroup, false);
            return new StickerPackListItemViewHolder(stickerPackRow);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof StickerPackListItemViewHolder) {
            // Calculate the actual sticker pack index (accounting for banner positions)
            int stickerPackIndex = calculateStickerPackIndex(position);
            
            // Check if the calculated index is valid and stickerPacks is not null
            if (stickerPacks != null && stickerPackIndex >= 0 && stickerPackIndex < stickerPacks.size()) {
                StickerPack pack = stickerPacks.get(stickerPackIndex);
                
                StickerPackListItemViewHolder holder = (StickerPackListItemViewHolder) viewHolder;
                
                // Tray icon
                holder.packTrayIcon.setImageURI(StickerPackLoader.getStickerAssetUri(pack.identifier, pack.trayImageFile));

                holder.titleView.setText(pack.name);
                holder.container.setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), StickerPackDetailsActivity.class);
                    intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true);
                    intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, pack);
                    view.getContext().startActivity(intent);
                });
                holder.animatedStickerPackIndicator.setVisibility(pack.animatedStickerPack ? View.VISIBLE : View.GONE);
                holder.packEmojisTextView.setVisibility(!Objects.equals(pack.packEmojis, "") ? View.VISIBLE : View.GONE);
                holder.packEmojisTextView.setText(pack.packEmojis);
            }
        }
    }

    @Override
    public int getItemCount() {
        // Calculate total items including banners
        if (stickerPacks == null) {
            return 0;
        }
        int bannerCount = stickerPacks.size() / BANNER_INTERVAL;
        return stickerPacks.size() + bannerCount;
    }

    private int calculateStickerPackIndex(int adapterPosition) {
        // Calculate how many banners appear before this position
        int bannersBefore = adapterPosition / BANNER_INTERVAL;
        return adapterPosition - bannersBefore;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    void setStickerPackList(List<StickerPack> stickerPackList) {
        if (stickerPackList != null) {
            this.stickerPacks = stickerPackList;
        } else {
            this.stickerPacks = new ArrayList<>();
        }
    }
}
