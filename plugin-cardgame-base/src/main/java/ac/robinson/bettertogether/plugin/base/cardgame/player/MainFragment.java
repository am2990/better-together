package ac.robinson.bettertogether.plugin.base.cardgame.player;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.cleveroad.fanlayoutmanager.callbacks.FanChildDrawingOrderCallback;

import ac.robinson.bettertogether.plugin.base.cardgame.R;
import ac.robinson.bettertogether.plugin.base.cardgame.models.CardDeck;
import ac.robinson.bettertogether.plugin.base.cardgame.models.CardDeckStatus;

/**
 * Created by t-apmehr on 6/6/2017.
 */


public class MainFragment extends Fragment {

    private FanLayoutManager fanLayoutManager;

    private CardsAdapter adapter;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the card deck for which it is intended
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvCards);

        FanLayoutManagerSettings fanLayoutManagerSettings = FanLayoutManagerSettings
                .newBuilder(getContext())
                .withFanRadius(true)
                .withAngleItemBounce(10)
                .withViewHeightDp(180)
                .withViewWidthDp(125)
                .build();

        fanLayoutManager = new FanLayoutManager(getContext(), fanLayoutManagerSettings);

        recyclerView.setLayoutManager(fanLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CardsAdapter(getContext());
        adapter.addAll(new CardDeck(getContext(), CardDeckStatus.OPEN, true).getmCards());

        adapter.setOnItemClickListener(new CardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, final View view) {
                if (fanLayoutManager.getSelectedItemPosition() != itemPosition) {
                    fanLayoutManager.switchItem(recyclerView, itemPosition);
                } else {
                    fanLayoutManager.straightenSelectedItem(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                onClick(view, fanLayoutManager.getSelectedItemPosition());
                            } else {
                                onClick(fanLayoutManager.getSelectedItemPosition());
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }
            }
        });

        recyclerView.setAdapter(adapter);

        recyclerView.setChildDrawingOrderCallback(new FanChildDrawingOrderCallback(fanLayoutManager));

        (view.findViewById(R.id.logo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fanLayoutManager.collapseViews();
            }
        });

    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClick(View view, int pos) {
//        FullInfoTabFragment fragment = FullInfoTabFragment.newInstance(adapter.getModelByPos(pos));
//
//        fragment.setSharedElementEnterTransition(new SharedTransitionSet());
//        fragment.setEnterTransition(new Fade());
//        setExitTransition(new Fade());
//        fragment.setSharedElementReturnTransition(new SharedTransitionSet());
//
//        getActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .addSharedElement(view, "shared")
//                .replace(R.id.root, fragment)
//                .addToBackStack(null)
//                .commit();
    }

    public void onClick(int pos) {
//        FullInfoTabFragment fragment = FullInfoTabFragment.newInstance(adapter.getModelByPos(pos));
//        getActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.root, fragment)
//                .addToBackStack(null)
//                .commit();
    }

    public boolean deselectIfSelected() {
        if (fanLayoutManager.isItemSelected()) {
            fanLayoutManager.deselectItem();
            return true;
        } else {
            return false;
        }
    }

}

