package edu.uoregon.bbird.rps;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Brian Bird on 7/15/2015.
 */

public class FirstFragment extends Fragment implements OnClickListener {

    private RpsGame game;
    private boolean twoPaneLayout;
    private EditText rpsEditText;
    private Button playButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.first_fragment, container, false);

        // Set this fragment to listen for the Play button's click event
        Button playButton = (Button) view.findViewById(R.id.playButton);
        playButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get a references from the host activity
        FirstActivity activity = (FirstActivity)getActivity();
        rpsEditText = (EditText) activity.findViewById(R.id.rpsEditText);

        // Make a new game object, use saved state if it exists

        if(savedInstanceState != null) {
            // Restore saved state
            Hand humanHand = Hand.values()[savedInstanceState.getInt("humanHand", 0)];
            rpsEditText.setText(humanHand.toString());
            Hand computerHand = Hand.values()[savedInstanceState.getInt("computerHand", 0)];
            game = new RpsGame(computerHand, humanHand);
        }
        else {
            game = new RpsGame();
        }
        // Give the host activity a reference to the game object
        game = new RpsGame();
        activity.setGame(game);

        // Check to see if FirstActivity has loaded a single or dual pane layout
        twoPaneLayout = activity.findViewById(R.id.second_fragment) != null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playButton) {

            // Get user's move
            String humanHand = rpsEditText.getText().toString().toLowerCase();
            if (humanHand.equals("")) {
                humanHand = "none";
            }
            game.setHumanHand(Hand.valueOf(humanHand));

            // Conditionally clear user's move from the EditText
            SharedPreferences savedValues = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if(savedValues.getBoolean("enable_reset_checkbox", true))
                rpsEditText.setText("");

            // Initiate computer's move
            if(!twoPaneLayout) {
                Intent intent = new Intent(getActivity(), SecondActivity.class);
                int humanHandNum = game.getHumanHand().ordinal();
                intent.putExtra("humanHand", humanHandNum);  // send state to 2nd activity
                startActivity(intent);
            } else {
                ((FirstActivity)getActivity()).makeComputerMove();

            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            outState.putInt("humanHand", game.getHumanHand().ordinal());
            outState.putInt("computerHand", game.getComputerHand().ordinal());
    }

}
