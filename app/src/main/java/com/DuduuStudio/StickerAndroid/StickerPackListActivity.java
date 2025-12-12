/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.DuduuStudio.StickerAndroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StickerPackListActivity extends AddStickerPackActivity {
    public static final String EXTRA_STICKER_PACK_LIST_DATA = "sticker_pack_list";
    private static final String TAG = "StickerPackListActivity";
    private LinearLayoutManager packLayoutManager;
    private RecyclerView packRecyclerView;
    private StickerPackListAdapter allStickerPacksListAdapter;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    private ArrayList<StickerPack> stickerPackList;

    // Character list components
    private RecyclerView characterRecyclerView;
    private CharacterAdapter characterAdapter;
    private List<Character> characterList;
    private Character selectedCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack_list);
        
        packRecyclerView = findViewById(R.id.sticker_pack_list);
        characterRecyclerView = findViewById(R.id.character_list);
        
        stickerPackList = getIntent().getParcelableArrayListExtra(EXTRA_STICKER_PACK_LIST_DATA);
        if (stickerPackList == null) {
            stickerPackList = new ArrayList<>();
        }
        
        // Initialize character list
        initializeCharacterList();
        
        // Setup RecyclerView layout manager first
        packLayoutManager = new LinearLayoutManager(this);
        packLayoutManager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                packRecyclerView.getContext(),
                packLayoutManager.getOrientation()
        );
        packRecyclerView.addItemDecoration(dividerItemDecoration);
        packRecyclerView.setLayoutManager(packLayoutManager);
        
        // Disable nested scrolling for sticker pack list since it's inside NestedScrollView
        packRecyclerView.setNestedScrollingEnabled(false);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void initializeStickerPackList() {
        if (allStickerPacksListAdapter == null) {
            allStickerPacksListAdapter = new StickerPackListAdapter(stickerPackList);
            packRecyclerView.setAdapter(allStickerPacksListAdapter);
        }
    }

    private void initializeCharacterList() {
        // Load characters from StickerContentProvider
        characterList = StickerContentProvider.getCharacters(this);
        Log.d(TAG, "Character list initialized with " + characterList.size() + " characters");
        
        // Setup character adapter
        characterAdapter = new CharacterAdapter(this, characterList);
        characterAdapter.setOnCharacterClickListener(new CharacterAdapter.OnCharacterClickListener() {
            @Override
            public void onCharacterClick(Character character, int position) {
                handleCharacterSelection(character, position);
            }

            @Override
            public void onAllCharactersClick() {
                clearCharacterSelection();
            }
        });
        
        // Setup character recycler view with horizontal layout
        LinearLayoutManager characterLayoutManager = new LinearLayoutManager(this);
        characterLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        characterRecyclerView.setLayoutManager(characterLayoutManager);
        characterRecyclerView.setAdapter(characterAdapter);
        
        // Disable nested scrolling for character list since it's inside NestedScrollView
        characterRecyclerView.setNestedScrollingEnabled(false);
        
        Log.d(TAG, "Character list setup completed");
    }

    private void handleCharacterSelection(Character character, int position) {
        if (character == null) {
            Log.w(TAG, "handleCharacterSelection called with null character");
            return;
        }
        
        // Update selection in adapter
        characterAdapter.setSelectedCharacter(position);
        
        // Update selected character
        selectedCharacter = character;
        
        // Filter sticker packs based on selected character
        List<StickerPack> filteredPacks = filterStickerPacksByCharacter(stickerPackList, selectedCharacter);
        
        // Update sticker pack list
        if (allStickerPacksListAdapter != null) {
            allStickerPacksListAdapter.setStickerPackList(filteredPacks);
            allStickerPacksListAdapter.notifyDataSetChanged();
        }
        
        Log.d(TAG, "Selected character: " + character.getName() + ", Filtered packs: " + filteredPacks.size());
    }

    // Add method to clear character selection
    private void clearCharacterSelection() {
        characterAdapter.clearSelection();
        selectedCharacter = null;
        
        // Show all sticker packs
        if (allStickerPacksListAdapter != null) {
            allStickerPacksListAdapter.setStickerPackList(stickerPackList);
            allStickerPacksListAdapter.notifyDataSetChanged();
        }
        
        Log.d(TAG, "Character selection cleared, showing all packs: " + (stickerPackList != null ? stickerPackList.size() : 0));
    }

    // Filter sticker packs by character
    private List<StickerPack> filterStickerPacksByCharacter(List<StickerPack> allStickerPacks, Character character) {
        if (allStickerPacks == null) {
            return new ArrayList<>();
        }
        
        if (character == null) {
            return allStickerPacks; // Return all if no character selected
        }

        List<StickerPack> filteredPacks = new ArrayList<>();
        String characterIdentifier = character.getIdentifier();

        for (StickerPack pack : allStickerPacks) {
            // Check if pack character matches mapped character
            if (pack != null && characterIdentifier != null && characterIdentifier.equals(pack.getCharacter())) {
                filteredPacks.add(pack);
            }
        }

        return filteredPacks;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Ensure adapter is initialized before starting async task
        if (allStickerPacksListAdapter == null) {
            initializeStickerPackList();
        }
        
        // Only start async task if we have sticker packs and adapter is initialized
        if (stickerPackList != null && !stickerPackList.isEmpty() && allStickerPacksListAdapter != null) {
            whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
            whiteListCheckAsyncTask.execute(stickerPackList.toArray(new StickerPack[0]));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
    }

    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, List<StickerPack>> {
        private final WeakReference<StickerPackListActivity> stickerPackListActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackListActivity stickerPackListActivity) {
            this.stickerPackListActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @Override
        protected final List<StickerPack> doInBackground(StickerPack... stickerPackArray) {
            final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity == null || stickerPackArray == null) {
                return new ArrayList<>();
            }
            for (StickerPack stickerPack : stickerPackArray) {
                if (stickerPack != null) {
                    stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(stickerPackListActivity, stickerPack.identifier));
                }
            }
            return Arrays.asList(stickerPackArray);
        }

        @Override
        protected void onPostExecute(List<StickerPack> stickerPackList) {
            final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity != null && stickerPackListActivity.allStickerPacksListAdapter != null) {
                try {
                    // Ensure we have a valid list
                    if (stickerPackList == null) {
                        stickerPackList = new ArrayList<>();
                    }
                    stickerPackListActivity.allStickerPacksListAdapter.setStickerPackList(stickerPackList);
                    stickerPackListActivity.allStickerPacksListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("StickerPackListActivity", "Error updating adapter: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sticker_pack_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
