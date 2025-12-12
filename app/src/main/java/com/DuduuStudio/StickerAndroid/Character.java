package com.DuduuStudio.StickerAndroid;

import android.os.Parcel;
import android.os.Parcelable;

public class Character implements Parcelable {
    private String identifier;
    private String name;
    private String image;
    private String description;
    private boolean isSelected;

    public Character(String identifier, String name, String image, String description) {
        this.identifier = identifier;
        this.name = name;
        this.image = image;
        this.description = description;
        this.isSelected = false;
    }

    protected Character(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        image = in.readString();
        description = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Character> CREATOR = new Creator<Character>() {
        @Override
        public Character createFromParcel(Parcel in) {
            return new Character(in);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(description);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
} 