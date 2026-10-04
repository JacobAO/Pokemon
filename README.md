# Pokemon List App

## Running the App

To run, open in a recent version of Android Studio that support AGP 9 and Gradle 9. The project
should use the bundled JDK. Sync and run on a device or emulator running Android 9 or higher.

## Implementation Notes

I chose to implement a basic list + detail screen that shows navigation including a default version
of a ListDetailScene which offers better usage of large screen sizes.

I used the paging3 library for easy handling of things like overall load state vs next page load
state. More complex paging could require a custom implementation but a basic one like this can make
good use of the library to ensure basic cases are handled correctly. I set up the pager to load 1
page in advance to help ensure the user _usually_ doesn't see a loading state.

I made a couple decisions regarding the API usage that might be different if I owned the server as
well:

- didn't use the "next" URL as it seems a little suspicious to load data from an effectively
  arbitrary URL, although I am technically doing this to show the images anyway ¯\_(ツ)_/¯
- trust that the names are effectively IDs meaning they will be unique in the list to not crash the
  LazyColumn and use the name to load details from a known URL, again instead of using an arbitrary
  server-given URL

I am caching the list data in the list ViewModel which will persist until the app session is ended
or the Activity is otherwise destroyed (e.g. system cleaning up memory). Detail screens get a
ViewModel
per screen instance (only one on the stack at a time)

I implemented pull to refresh when data is present even though this data is quite static. I did this
to show how it would look, but for a production level implementation I might not include it or make
sure the user can pull to refresh in any state. I did try to handle static load/error states vs pull
to refresh load/error states harmoniously.

For time savings and because the app only has a few UI components, I kept the default material theme
that the new Android project template provides which is not complete. The given theme at least has
enough defined for some simple text styles and light/dark mode support

## Libraries used

Since the app is simple, I just used what I consider the default libraries for each architecture
piece
that are well maintained and supported.

- DI: Hilt
- Networking: Retrofit/OkHttp
- Image loading: Coil
- UI: basic material components with default themes and text styles
- Logging: Timber
- Navigation: androidx nav3
- Paging: androidx paging3

## Future Improvements

If I wanted to make this app more complete and implement it to production quality, I might include:

- Unit tests: of course. I did not include them just to show more usage of user-facing features
- Better list/detail scene layout like showing an empty state detail pane when no pokemon is
  selected, highlighting the selected pokemon in the list, or custom layout for foldables
- Cooler navigation transition animations like shared-element transition with the pokemon name
- Scroll to top button on the list since it's a huge list
- Edge to edge is enabled but not fully utilized. Let content appear below bottom navigation bar
  insets
- Use R8 for code obfuscation and build optimization
- Deep link support for pokemon details
- DB or HTTP-layer caching
- basic accessibility should work by using normal components and things like marking the list items
  as buttons, but I would do more robust testing for a production level implementation
- logging errors and usage data to an external system