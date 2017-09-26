# Website CSS
This documentation explains the makeup of the css styles in Carbon LDP's website, and contains a guide on the existing customized classes and how to create them for future pages.

## Composition of Carbon LDP's CSS

1. **[Semantic UI](http://semantic-ui.com/)** is a front-end development framework.
2. **Customized classes** named using:
    - **[BEM](http://getbem.com/introduction/) methodology**, are classes that personalize the view of an specific element or block that doesn't use Semantic UI classes.
        + **Blocks/independent elements** classes, are classes applied to standalone entities.
        + **Layout** classes, described more in depth in the **layout classes** section
        + **Background/text** classes, are classes that contain different pre defined background and text colors
    - **Semantic UI naming methodology**, are classes that extend existing Semantic UI classes for specific styles in elements that use Semantic UI styles __throughout the website__.


### Layout classes
Layout classes are the same as block classes, however we give them their own special place in this documentation, since they are the building blocks of our website, and will remain independent from all other blocks found in it.
The website is divided in three consistent blocks encapsulated by a general block through all its pages:
- `site`: the entire webpage, contains display styles.
    + `header`: contains logo and navigation menu ( handled by Semantic UI through ui top fixed menu classes)
    + `site-content`: everything in between the header and footer, contains display styles to have a fixed footer.
    + `footer`: contains logo, disclaimer and social media presence. (handled by Semantic UI through ui vertical footer classes)


## Naming Conventions

### [BEM](http://getbem.com/introduction/)

BEM methodology helps you make your classes reusable and modular. This is a basic overview, for more information on BEM go to the [official website](http://getbem.com/introduction/)

**Our conventions**

- **blockName**: standalone entity that is meaningful on its own. ( Basic style for objects in the computer view, for special styles in mobile/tablet view use a modifier ). e.g. `mainMenu`

- **blockName--modifier**: the modifier is a flag on a block or element, that denotes a change in appearance or behavior e.g. `mainMenu--mobile`

- **blockName-element**: a part of a block that has no standalone meaning and is semantically tied to its block. e.g. `mainMenu-button`

- **blockName-element--modifier**: e.g. `mainMenu-button--small`

- **colorNameBG**: defines the a background color, named using the color name followed by the suffix **BG** (background) (all backgrounds provide white text.)
    ```css
    .dullLimeBG{
      background-color: #53B948;
      color: #ffffff;
    }
    ```

- **colorNameText**: defines the color of the text, named using the color name followed by the suffix **Text**
    `colorNameText`
    ```css
    .californiaText{
      color: #F89728;
    }
    ```

### Semantic UI
This is not exactly naming convention, but you may found chained classes in our css, that follow the pattern of Semantic UI classes. Why?

Because even though Semantic UI is a solid, customizable framework, there may be instances in which we wish to customized one of its elements to fit our styling needs.
- Add a customized class to extend and overwrite the styles you wish to change, this usually apply to changes in text or background color.
    You should follow the BEM naming convention described above to name the new classes, that will be chained to the Semantic UI classes that need to be overwritten.

    e.g.
    The button used in home. Semantic UI offers you a wide variety of colored buttons. However the specific combination we needed: white button with california(orange) letters is not available.
    In this instance we use the names of the Semantic UI classes to overwrite the styles we wish to change in the element.
    ```css
    /* These are additions to .ui.button class of Semantic UI, a completely white button, with colored provided by class .ui.button.<color>CT */
    .ui.whiteBG.button, .ui.whiteBG.buttons .ui.button {
      background-color: #fff;
      text-shadow: none;
      background-image: none;
    }
    .ui.button.californiaText {
      color: #F89728;
    }
    .ui.button.button--mobile, .ui.buttons.buttons--mobile{
      padding:0;
    }
```
In this example `.whiteBG`, `.californiaText` and `.button--mobile` are customized classes, named following the established conventions. However they will not have any effect on the button we are trying to style if we do not overwrite the `.ui.button` default styles.

## How to name your new classes

Before creating a new class, you should analyze:
- Your existing classes
- The type of object you want to style


Follow this questions to define the correct class name:

1. **Is there already a class that style the same block/element?** No? Skip to 2.

    - Yes, is it a layout class? Yes? Skip to 2.
        + No? Use the basic class adding a modifier, as long as it is not a layout class.

        ```css
        /* We add a modifier for mobile view to the block and element classes: masthead and masthead-title */
        /* Masthead is a block */
          .masthead{
            margin-top: 25vh;
          }

        /* NEW CLASS */
          .masthead--mobile{
            margin-top: 27vh;
            padding: 16px;
          }
        /* Title is an element of the block masthead */
          .masthead-title{
            color: #e1dfdf;
            font-size: 3rem;
            text-align: center;
          }

        /* NEW CLASS */
          .masthead-title--mobile{
            font-size: 2.2rem;
          }

          /* We add a modifier to remove the margins in a banner, so that the text fills the entire width */
          .banner{
            padding: 100px 0;
            text-align: center;
            max-width: 600px;
            margin: auto;
          }

        /* NEW CLASS */
        .banner--fluid {
            margin: 0;
            max-width:100%;
            padding: 20px 10px;
        }
        ```

2. **Is the object, actually the layout of the page? or is the class that I found a layout class?** No? Skip to
3.

    - Yes? *Avoid* classes directly applied to the layout, if it is absolutely necessary then name them after the already existing class of the layout adding a descriptive modifier. (**do not create new layout classes**) See Layout Classes section.

        e.g. The class will provide different margins for the license page, use class `site-content--license`
        basic page

        ```css
        .site-content {
            flex: 1 0 auto;
            align-items: center;
            margin-top: 74px;
        }


        /* NEW CLASS */
        .site-content--license {
        margin: 0;
        }
        ```
        ```html
        <!-- all pages -->
            <div class="site-content">
                content of every page
            </div>
        <!--license page-->
            <div class="site-content site-content--license">
                content of home here
            </div>
        ```

3. **Is the object using Semantic UI classes?** No? Skip to 4.

    - Yes? Can the object be encapsulated in a customized class? Yes? Skip to 4.
    - No? If it cannot be encapsulated then it must be overwritten, refer to the sub section Semantic UI in Naming conventions.

4. **Is the object a standalone object? or can it be used by itself independently?** No? Skip to 5.

    - Yes? Create a new class following BEM convention:
        + The name should be descriptive
        + The class should be for the computer view, for tablet/mobile view create a class with a modifier
        + If needed, use cammelCase for the class name
        + Do not reuse names, if you are reusing a name, it may mean that you didn't analyze your object and your classes correctly and should go back to question 1. and add a modifier.

        e.g. You have a sidebar on your page, a class for a sidebar, this sidebar can appear in any number of pages, regardless of the content of the page itself. It must have the same appearance always to ensure consistency in our site

        ```css
        .sidebar{
            /* General style of the sidebar: margins, padding, position, display, etc. */
        }
        ```

5. **Does the object only have meaning or can only be used within another object?** No? Perhaps you skipped a question! Please, review them again.

    - Yes? You are talking about an element, you should analyze the block to which the element belongs

        1. Is there a class for this element in the block? No, skip to 5.b.

            - Yes? Add a modifier, following BEM conventions as shown in 1.

                e.g. You have a sidebar, that has an element: title of the section. In the css there already is a class named sidebar-sectionTitle, but you need this title to appear bold when selected.

                ```css
                .sidebar-sectionTitle{
                    font-size: 16px;
                    margin: 10px;
                }

                .sidebar-sectionTitle--selected{
                    font-weight: bold;
                }
                ```

        2. Create a class following BEM conventions the name will be `blockName-elementName`

            - The element name should be descriptive
            - The class should be for the computer view, for tablet/mobile view create a class with a modifier
            - If needed, use cammelCase for the class name
            - Do not reuse element names in the same block class, if you are reusing a name, it may mean that you didn't analyze your object and your classes correctly and should go back to question 5.a. and add a modifier.

                e.g. You have a sidebar in your website and the sidebar has a title, and titles of sections and subsections, these titles can not be used without the sidebar, so you can conclude they are elements.

                ```css
                .sidebar-title{
                    /* Specific css for the title: margins, padding, font-weight, font-size */
                }

                .sidebar-sectionTitle {
                   /* Specific css for the titles of the sections: margins, padding, font-weight, font-size */
                }

                .sidebar-subsectionTitle {
                    /* Specific css for the titles of the subsections: margins, padding, font-weight, font-size */
                }
                ```

## Remember!

### Special pages
Ideally the layout classes must remain the same always, however if it is absolutely necessary to add a page with different styles in the layout, ALWAYS use modifiers. Do not create classes with different names than the ones provided. As our site grows it could be hard to look for the specific class that is impacting the layout of an specific page.

### Style for different devices
By default all the elements, block styles will correspond to the computer view. For other devices such as tablet or mobile add a modifier to the basic class Name (e.g. className--tablet, className--mobile)

### Mixing semantic ui and carbon styles

Avoid as far as possible, the combination of carbon ldp's and Semantic UI classes in the same element. Try to encapsulate either internally or externally the element you wish to customized.
If there is no other way to achieve the desired style, follow the naming conventions described above.

**Semantic UI's grid collection**

When using semantic ui grids encapsulate your customize classes in an internal element, do this, to facilitate edition, debugging process of styles and to avoid semantic ui's classes unexpected behaviors.
