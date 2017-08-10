# Social Text View
### Summary
An Android TextView that uses a very accurate and custom LinkMovementMethod and detects social
media lingo; *i.e.* #hashtags, @mentions, phone numbers, emails, and urls.

`LinkMovementMethod` has a small bug (at least I think so); it clicks on a link when
you press near it sometimes. The custom implementation I had built, `LinkTouchMovementMethod`, 
fixes that by using a `RectF` to store the bounds of each link and
making sure when you touch it, it's within the bounds before triggering the callback.

### Example
![Screenshot](https://github.com/tylersuehr7/socialtextview/blob/master/img_example.png "Screenshot")

#### Example Layout Usage:
```xml
<com.tylersuehr.socialtextview.SocialTextView
        android:id="@+id/social_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="#hashtag, @mention, email@gmail.com, (412) 111-2222, http://www.url.com"
        app:hashtagColor="@color/colorAccent"
        app:mentionColor="@color/colorAccent"
        app:phoneColor="@color/colorAccent"
        app:emailColor="@color/colorAccent"
        app:urlColor="@color/colorAccent"
        app:selectedColor="@color/colorPrimary"
        app:linkModes="hashtag|mention|email|phone|url"/>
```
#### Example Java Usage (With XML Layout):
```java
public class MainActivity extends AppCompatActivity implements SocialLinkClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String linkText = "#hashtag, @mention, email@yahoo.com, (412) 111-2222, http://www.url.net";

        // Setup SocialTextView
        SocialTextView textView = (SocialTextView)findViewById(R.id.social_text);
        textView.setSocialLinkClickListener(this);
        textView.setLinkText(linkText);
    }

    @Override
    public void onSocialLinkClicked(LinkMode mode, String matchedText) {
        Toast.makeText(this, "Touched: "  + mode.toString(), Toast.LENGTH_SHORT).show();
    }
}
```
#### Example Java Usage (Without XML Layout):
```java
public class MainActivity extends AppCompatActivity implements SocialLinkClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String linkText = "#hashtag, @mention, email@yahoo.com, (412) 111-2222, http://www.url.net";

        // Setup SocialTextView
        SocialTextView textView = new SocialTextView(this);
        textView.setSocialLinkClickListener(this);
        textView.setLinkText(linkText);

        addView(textView);
    }

    @Override
    public void onSocialLinkClicked(LinkMode mode, String matchedText) {
        Toast.makeText(this, "Touched: "  + mode.toString(), Toast.LENGTH_SHORT).show();
    }
}
```
