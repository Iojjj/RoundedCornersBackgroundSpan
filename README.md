# Rounded Corners Background Span
![Rounded Corners Background Span](/images/demo.png)


## Setup and usage

To include this library to your project add dependency in **build.gradle** file:

```groovy
dependencies {
    compile 'com.github.iojjj:rcbs:1.0.0'
}
```

There are two ways to create spannable string: using **EntireTextBuilder** or **TextPartsBuilder**.

```JAVA
final Spanned spanned = new RoundedCornersBackgroundSpan.EntireTextBuilder(this, "some text")
    .setTextPadding(textPadding)
    .setCornersRadius(cornersRadius)
    .setTextPaddingRes(R.dimen.textPadding)
    .setCornersRadiusRes(R.dimen.cornersRadius)
    .addBackground(Color.RED, 0, 1)
    .addBackgroundRes(R.color.colorAccent, 2, 3)
    .build();
textView.setText(spanned);

...

final Spannable spanned = new RoundedCornersBackgroundSpan.TextPartsBuilder(this)
    .setTextPadding(textPadding)
    .setCornersRadius(cornersRadius)
    .setTextPaddingRes(R.dimen.textPadding)
    .setCornersRadiusRes(R.dimen.cornersRadius)
    .setSeparator(RoundedCornersBackgroundSpan.DEFAULT_SEPARATOR)
    .addTextPart("part1", Color.RED)
    .addTextPart(R.string.part2, R.color.colorAccent)
    .addTextPart("part3. no background")
    .addTextPart(R.string.part4_no_background)
    .build()
textView.setText(spanned);
```

As an entire text or text parts you can pass any `CharSequence`, even another `Spanned` strings with their own spans!

###### Restrictions: 
* You must always apply padding, lines spacing and shadow to your TextView. See sample app for example.
* Shadow radius must be equal to text padding.
* Prefer transparent colors for shadow. You can set shadow via code `TextView.setShadowLayer(padding, 0, 0, Color.TRANSPARENT);`. 
* You can't set transparent shadow color via XML. Use non-zero colors, for example, `#00000001`.
* EntireTextBuilder is quite difficult to use because you need to add text separators on your own and calculate proper start and end background offsets. Prefer TextPartsBuilder instead.

<br />
## Changelog

| Version | Changes                         |
| --- | --- |
| v.1.0.0 | First public release            |

<br />
## Support

You can support this library by creating a pull request with bug fixes and/or new features on `develop` branch. Any pull requests on `master` branch will be removed. 

<br />
## License
* * *
    The MIT License (MIT)
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.