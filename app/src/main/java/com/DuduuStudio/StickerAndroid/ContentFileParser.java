/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.DuduuStudio.StickerAndroid;

import android.text.TextUtils;
import android.util.JsonReader;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class ContentFileParser {

    @NonNull
    static List<StickerPack> parseStickerPacks(@NonNull InputStream contentsInputStream) throws IOException, IllegalStateException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(contentsInputStream))) {
            return readStickerPacks(reader);
        }
    }

    @NonNull
    static List<Character> parseCharacters(@NonNull InputStream contentsInputStream) throws IOException, IllegalStateException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(contentsInputStream))) {
            return readCharacters(reader);
        }
    }

    @NonNull
    private static List<StickerPack> readStickerPacks(@NonNull JsonReader reader) throws IOException, IllegalStateException {
        List<StickerPack> stickerPackList = new ArrayList<>();
        String androidPlayStoreLink = null;
        String iosAppStoreLink = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if ("android_play_store_link".equals(key)) {
                androidPlayStoreLink = reader.nextString();
            } else if ("ios_app_store_link".equals(key)) {
                iosAppStoreLink = reader.nextString();
            } else if ("sticker_packs".equals(key)) {
                reader.beginArray();
                while (reader.hasNext()) {
                    StickerPack stickerPack = readStickerPack(reader);
                    stickerPackList.add(stickerPack);
                }
                reader.endArray();
            } else if ("character_list".equals(key)) {
                // Skip character_list as it's handled separately
                reader.skipValue();
            } else {
                throw new IllegalStateException("unknown field in json: " + key);
            }
        }
        reader.endObject();
        if (stickerPackList.size() == 0) {
            throw new IllegalStateException("sticker pack list cannot be empty");
        }
        for (StickerPack stickerPack : stickerPackList) {
            stickerPack.setAndroidPlayStoreLink(androidPlayStoreLink);
            stickerPack.setIosAppStoreLink(iosAppStoreLink);
        }
        // Shuffle the sticker pack list to randomize the order
        java.util.Collections.shuffle(stickerPackList);
        return stickerPackList;
    }

    @NonNull
    private static List<Character> readCharacters(@NonNull JsonReader reader) throws IOException, IllegalStateException {
        List<Character> characterList = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if ("character_list".equals(key)) {
                reader.beginArray();
                while (reader.hasNext()) {
                    Character character = readCharacter(reader);
                    characterList.add(character);
                }
                reader.endArray();
            } else {
                // Skip other fields
                reader.skipValue();
            }
        }
        reader.endObject();
        
        // Shuffle the character list to randomize the order
        java.util.Collections.shuffle(characterList);
        
        return characterList;
    }

    @NonNull
    private static Character readCharacter(@NonNull JsonReader reader) throws IOException, IllegalStateException {
        reader.beginObject();
        String identifier = null;
        String name = null;
        String image = null;
        String description = null;
        
        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case "identifier":
                    identifier = reader.nextString();
                    break;
                case "name":
                    name = reader.nextString();
                    break;
                case "image":
                    image = reader.nextString();
                    break;
                case "description":
                    description = reader.nextString();
                    break;
                default:
                    reader.skipValue();
            }
        }
        
        if (TextUtils.isEmpty(identifier)) {
            throw new IllegalStateException("character identifier cannot be empty");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalStateException("character name cannot be empty");
        }
        if (TextUtils.isEmpty(image)) {
            throw new IllegalStateException("character image cannot be empty");
        }
        if (TextUtils.isEmpty(description)) {
            description = ""; // Make description optional
        }
        
        reader.endObject();
        return new Character(identifier, name, image, description);
    }

    @NonNull
    private static StickerPack readStickerPack(@NonNull JsonReader reader) throws IOException, IllegalStateException {
        reader.beginObject();
        String identifier = null;
        String name = null;
        String publisher = "Duduu Friends";
        String trayImageFile = "Main.png";
        String publisherEmail = "your.email@example.com";
        String publisherWebsite = "https://www.instagram.com/duduufriends/";
        String privacyPolicyWebsite = "https://www.instagram.com/duduufriends/";
        String licenseAgreementWebsite = "https://www.instagram.com/duduufriends/";
        String imageDataVersion = "";
        String packEmojis = "";
        boolean avoidCache = false;
        boolean animatedStickerPack = false;
        String character = "Null";
        List<Sticker> stickerList = null;
        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case "identifier":
                    identifier = reader.nextString();
                    break;
                case "name":
                    name = reader.nextString();
                    break;
                case "character":
                    character = reader.nextString();
                    break;
                case "stickers":
                    stickerList = readStickers(reader);
                    break;
                case "image_data_version":
                    imageDataVersion = reader.nextString();
                    break;
                case "pack_emojis":
                    packEmojis = reader.nextString();
                    break;
                case "avoid_cache":
                    avoidCache = reader.nextBoolean();
                    break;
                case "animated_sticker_pack":
                    animatedStickerPack = reader.nextBoolean();
                    break;
                default:
                    reader.skipValue();
            }
        }
        if (TextUtils.isEmpty(identifier)) {
            throw new IllegalStateException("identifier cannot be empty");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalStateException("name cannot be empty");
        }
        if (TextUtils.isEmpty(publisher)) {
            throw new IllegalStateException("publisher cannot be empty");
        }
        if (TextUtils.isEmpty(trayImageFile)) {
            throw new IllegalStateException("tray_image_file cannot be empty");
        }
        if (stickerList == null || stickerList.size() == 0) {
            throw new IllegalStateException("sticker list is empty");
        }
        if (identifier.contains("..") || identifier.contains("/")) {
            throw new IllegalStateException("identifier should not contain .. or / to prevent directory traversal");
        }
        if (TextUtils.isEmpty(imageDataVersion)) {
            throw new IllegalStateException("image_data_version should not be empty");
        }
        reader.endObject();
        final StickerPack stickerPack = new StickerPack(identifier, name, publisher, trayImageFile, publisherEmail, publisherWebsite, privacyPolicyWebsite, licenseAgreementWebsite, imageDataVersion, packEmojis, avoidCache, animatedStickerPack, character);
        stickerPack.setStickers(stickerList);
        return stickerPack;
    }

    @NonNull
    private static List<Sticker> readStickers(@NonNull JsonReader reader) throws IOException, IllegalStateException {
        reader.beginArray();
        List<Sticker> stickerList = new ArrayList<>();

        while (reader.hasNext()) {
            reader.beginObject();
            String imageFile = null;
            List<String> emojis = new ArrayList<>(StickerPackValidator.EMOJI_MAX_LIMIT);
            while (reader.hasNext()) {
                final String key = reader.nextName();
                if ("image_file".equals(key)) {
                    imageFile = reader.nextString();
                } else if ("emojis".equals(key)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        String emoji = reader.nextString();
                        if (!TextUtils.isEmpty(emoji)) {
                            emojis.add(emoji);
                        }
                    }
                    reader.endArray();

                } else {
                    throw new IllegalStateException("unknown field in json: " + key);
                }
            }
            reader.endObject();
            if (TextUtils.isEmpty(imageFile)) {
                throw new IllegalStateException("sticker image_file cannot be empty");
            }
            if (!imageFile.endsWith(".webp")) {
                throw new IllegalStateException("image file for stickers should be webp files, image file is: " + imageFile);
            }
            if (imageFile.contains("..") || imageFile.contains("/")) {
                throw new IllegalStateException("the file name should not contain .. or / to prevent directory traversal, image file is:" + imageFile);
            }
            stickerList.add(new Sticker(imageFile, emojis));
        }
        reader.endArray();
        return stickerList;
    }
}
