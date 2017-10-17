# Social Text View
A custom Android TextView that highlights social media lingo (#hashtags, @mentions, phone, emails, and urls).

<img src="https://github.com/tylersuehr7/socialtextview/blob/master/docs/screen_links.png" width="200">

Core features of this library:
* Highlight social media lingo in text
* Clickable social media lingo in text

`LinkMovementMethod` has a small bug (at least I think so); it clicks on a link when
you press near it sometimes. The custom implementation I had built, `LinkTouchMovementMethod`, 
fixes that by using a `RectF` to store the bounds of each link and
making sure when you touch it, it's within the bounds before triggering the callback.

## Using the Social Text View
The basic usage of this library is to highlight, and make clickable, whenever social media lingo appears in text. To achieve this functionality, you'll need to use the `SocialTextView`.

### Using in an XML layout
`SocialTextView` can be used in any ViewGroup and supports all available width and height attributes that `TextView` does. Simple usage is shown here:
```xml
<com.tylersuehr.socialtextview.SocialTextView
        android:id="@+id/social_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@tylersuehr7, Android is awesome! #android"
        app:hashtagColor="#757575"
        app:mentionColor="#757575"
        app:linkModes="hashtag|mention"/>
```

Here is a table of all the XML attributes available for this view:

Attribute | Type | Summary
:---: | :---: | ---
`app:hashtagColor` | `color` | Sets the text color of a hashtag link in the text.
`app:mentionColor` | `color` | Sets the text color of a mention link in the text.
`app:phoneColor` | `color` | Sets the text color of a phone number link in the text.
`app:emailColor` | `color` | Sets the text color of an email link in the text.
`app:urlColor` | `color` | Sets the text color of a web url link in the text.
`app:selectedColor` | `color` | Sets the text color of a selected link in the text.
`app:linkModes` | `int` | Sets flags for which types of links the text should show. i.e: "hashtag|mention|email|phone|url".

### Using in Java code
`SocialTextView` can be programmatically added into any ViewGroup. Simple usage in an Activity is shown here:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Text to display in the view
    String example = "Really neat stuff @tylersuehr7! #Android #GitHub";
    
    // Create an instance of the view and set the text above
    SocialTextView textView = new SocialTextView(this);
    textView.setLinkText(example);
    // set any other properties you want social text view
    
    setContentView(textView);
}
```

Here is a table of all the accessible attributes available for this view:

Method | Summary
--- | ---
`setLinkText(String)` | Checks the given text for any available links and displays it.
`appendLinkText(String)` | Checks the given text for any available links and appends it.
`setOnLinkClickListener(OnLinkClickListener)` | Sets a listener for when available links are clicked.
