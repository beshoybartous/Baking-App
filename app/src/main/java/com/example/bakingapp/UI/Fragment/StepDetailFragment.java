package com.example.bakingapp.UI.Activity.Fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.bakingapp.Models.Step;
import com.example.bakingapp.R;
import com.example.bakingapp.databinding.FragmentStepsDetailsBinding;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener{

    private final static String STEPS="GET_STEPS";
    private final static String STEPS_ID="GET_STEPS_ID";
    private final static String CURRENT_STEP="GET_CURRENT_STEP";
    private static final String PLAYER_CURRENT_POS_KEY ="GET_PLAYER_CURRENT_POS_KEY" ;
    private static final String PLAYER_IS_READY_KEY = "GET_PLAYER_IS_READY_KEY";
    private final static String DEVICE_TYPE="GET_DEVICE_TYPE";

    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    ArrayList<Step> steps;
    Step step;
    int stepID;
    private boolean mTwoPane=false;

    AppCompatActivity appCompatActivity;
    FragmentStepsDetailsBinding fragmentStepsDetailsBinding;
    private boolean playState=false;
    private long playPosition=0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         fragmentStepsDetailsBinding = DataBindingUtil.
                inflate( inflater, R.layout.fragment_steps_details,container,false );

        if (getArguments() != null&&savedInstanceState==null) {
            steps = getArguments().getParcelableArrayList( STEPS );
            stepID = getArguments().getInt( STEPS_ID );
            step=getArguments().getParcelable( CURRENT_STEP );
            mTwoPane=getArguments().getBoolean( DEVICE_TYPE );
            initializeMediaSession();
            checkStepPosition();
            setExoPlayerVide();
        }
        if(savedInstanceState!=null){
            steps = savedInstanceState.getParcelableArrayList( STEPS );
            stepID = savedInstanceState.getInt( STEPS_ID );
            step=savedInstanceState.getParcelable( CURRENT_STEP );
            playState=savedInstanceState.getBoolean(PLAYER_IS_READY_KEY);
            playPosition=savedInstanceState.getLong(PLAYER_CURRENT_POS_KEY);
            initializeMediaSession();
            checkStepPosition();
            setExoPlayerVide();
        }
        if(!mTwoPane) {
            appCompatActivity = (AppCompatActivity) getActivity();
            appCompatActivity.setSupportActionBar( (Toolbar) fragmentStepsDetailsBinding.toolbar );
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setHomeAsUpIndicator( R.drawable.back );
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled( true );
                appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled( true );
                appCompatActivity.getSupportActionBar().setTitle( step.getShortDescription() );

                ((Toolbar) fragmentStepsDetailsBinding.toolbar).setNavigationOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        appCompatActivity.onBackPressed();

                    }
                } );
            }
        }


        fragmentStepsDetailsBinding.description.setText( step.getDescription() );
        checkStepPosition();
        fragmentStepsDetailsBinding.buttonNextStep.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepID++;
                selectedStep();

            }
        } );
        fragmentStepsDetailsBinding.buttonPreviousStep.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepID--;
                selectedStep();
            }
        } );
        final View rootView=fragmentStepsDetailsBinding.getRoot();
        return rootView;
    }


    public void selectedStep(){
        checkStepPosition();
        step=steps.get( stepID );
        playState=false;
        playPosition=0;
        if(!mTwoPane) {
            appCompatActivity.getSupportActionBar().setTitle( step.getShortDescription() );
        }
        fragmentStepsDetailsBinding.description.setText( step.getDescription() );
        if(checkExoPlayer())
            releasePlayer();
        setExoPlayerVide();
    }
    @SuppressLint("RestrictedApi")
    public void checkStepPosition( ){

        if(stepID==0){
            fragmentStepsDetailsBinding.buttonPreviousStep.setVisibility( View.GONE );
        }
        else{
            fragmentStepsDetailsBinding.buttonPreviousStep.setVisibility( View.VISIBLE );
        }
        if(stepID==steps.size()-1){
            fragmentStepsDetailsBinding.buttonNextStep.setVisibility( View.GONE );
        }
        else{
            fragmentStepsDetailsBinding.buttonNextStep.setVisibility( View.VISIBLE );

        }
    }

    public boolean checkExoPlayer(){
        if(mExoPlayer!=null)
            return true;
        else
            return false;
    }

    public void setExoPlayerVide(){
        if(!step.getVideoURL().isEmpty()) {
            initializePlayer( Uri.parse( step.getVideoURL())  );
            fragmentStepsDetailsBinding.noVideo.setVisibility( View.GONE );
            fragmentStepsDetailsBinding.playerView.setVisibility( View.VISIBLE );

        }
        else if(!step.getThumbnailURL().isEmpty()) {
            initializePlayer(  Uri.parse( step.getThumbnailURL() ) );
            fragmentStepsDetailsBinding.noVideo.setVisibility( View.GONE );
            fragmentStepsDetailsBinding.playerView.setVisibility( View.VISIBLE );

        }
        else{
            fragmentStepsDetailsBinding.noVideo.setVisibility( View.VISIBLE );
            fragmentStepsDetailsBinding.playerView.setVisibility( View.GONE );
        }
    }


    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), StepDetailFragment.class.getSimpleName());

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initializePlayer( Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            fragmentStepsDetailsBinding.playerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener( StepDetailFragment.this );

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.seekTo( playPosition );
            mExoPlayer.setPlayWhenReady(playState);
        }
    }

    public void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }
    public void releaseSession() {
        mMediaSession.setActive(false);

    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(checkExoPlayer()) {
            playState=mExoPlayer.getPlayWhenReady();
            playPosition=Math.max( 0, mExoPlayer.getCurrentPosition());
            releasePlayer();
        }
        releaseSession();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putLong( PLAYER_CURRENT_POS_KEY,playPosition );
        outState.putBoolean( PLAYER_IS_READY_KEY, playState );
        outState.putInt( STEPS_ID,stepID );
        outState.putParcelable( CURRENT_STEP,step );
        outState.putParcelableArrayList( STEPS,steps );
    }


}
