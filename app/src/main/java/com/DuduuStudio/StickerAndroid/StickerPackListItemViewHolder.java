/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.DuduuStudio.StickerAndroid;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class StickerPackListItemViewHolder extends RecyclerView.ViewHolder {

    final View container;
    final ImageView packTrayIcon;
    final TextView titleView;
    final ImageView animatedStickerPackIndicator;
    final TextView packEmojisTextView;

    StickerPackListItemViewHolder(final View itemView) {
        super(itemView);
        container = itemView;
        packTrayIcon = itemView.findViewById(R.id.tray_image);
        titleView = itemView.findViewById(R.id.sticker_pack_title);
        animatedStickerPackIndicator = itemView.findViewById(R.id.sticker_pack_animation_indicator);
        packEmojisTextView = itemView.findViewById(R.id.sticker_pack_emojis_text);
    }
}