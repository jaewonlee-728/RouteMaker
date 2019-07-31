package com.example.oose.routemaker;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsFeedActivityTest extends ActivityInstrumentationTestCase2<NewsFeedActivity> {

    NewsFeedActivity activity;

    public NewsFeedActivityTest() {
        super(NewsFeedActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra("userId", "jlee381@jhu.edu");
        setActivityIntent(intent);
        activity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @SmallTest
    public void testViewNotNull() throws Exception {
        TextView welcomeMessage = (TextView) activity.findViewById(R.id.hello_text);
        TextView cityMessage = (TextView) activity.findViewById(R.id.featured_city_message);
        ImageView cityImageView = (ImageView) activity.findViewById(R.id.city_image);

        assertNotNull(welcomeMessage);
        assertNotNull(cityMessage);
        assertNotNull(cityImageView);
    }

    @SmallTest
    public void testDrawerNotNull() throws Exception {
        ListView mDrawerList = (ListView) activity.findViewById(R.id.left_drawer_newsfeed);
        DrawerLayout mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout_newsfeed);
        assertNotNull(mDrawerList);
        assertNotNull(mDrawerLayout);
    }

    @SmallTest
    public void testIntentCorrect() throws Exception {
        String userId = activity.getIntent().getStringExtra("userId");

        assertEquals(userId, "jlee381@jhu.edu");
    }
}

