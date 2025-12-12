/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.DuduuStudio.StickerAndroid;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

class InstagramBannerViewHolder extends RecyclerView.ViewHolder {

    final ImageView bannerImage;

    InstagramBannerViewHolder(final View itemView) {
        super(itemView);
        bannerImage = itemView.findViewById(R.id.instagram_banner_image);
        
        // Set click listener to open Instagram website
        itemView.setOnClickListener(v -> {
            String websiteUrl = v.getContext().getString(R.string.app_website);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
            v.getContext().startActivity(intent);
        });
    }
} 