# STS Compendium Filters + Effekseer

This mod (formerly known as STS-Effekseer) adds the following features:

- An in-game top panel button that opens a card pool screen with a grid of the cards currently available in your run. You may view upgrades for these cards and show/hide the available colorless cards.
- A card filters menu for the aforementioned grid as well as the card compendium and the master deck screen. You have the option of filtering cards by the mod they came from, their attributes (i.e. card type, rarity, and cost), and their keywords. You will also be shown the number of cards matching the given filters for each keyword. This menu can be accessed by clicking on the "Filters" button at the top-right part of the screen on said pages.
- An overhaul of the card compendium screen that makes the sorting header and color buttons always visible even when you scroll through the compendium (This can be disabled in the mod's settings)
- A mod settings menu that can be accessed mid-run. It can be configured to show all mod settings or to only show certain mod settings.

Both the card pool and the card filters components come with hooks that allow modders to render custom components within those screens. The card filter also allows modders to define their own custom filters for their custom cards.

This mod also comes with the following features:
- Support for playing Effekseer animations in Slay the Spire.
- Support for colorizing Effekseer animations and images in general with the colorful-gdx library, which offers a greater degree of freedom with color manipulation.
- Support for customizing the fonts used by this mod and the base game, as well as setting up your own fonts.

You can find Effekseer, along with some sample animations, at https://effekseer.github.io/en/.

These components are based off of code from the Animator. You can find updates on the Animator as well as this mod at the Animator discord: https://discord.gg/SmHMmJR

## **Requirements**
- BaseMod (5.37.1+)
- ModTheSpire (3.2.2+)
- STSLib (2.0.0+)

In addition, this library only works on Windows and Linux machines.

## **Credits**
- Code adapted from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SrJohnathan/gdx-effekseer
- DLL Libraries created from https://github.com/effekseer/EffekseerForMultiLanguages
- Uses functions from https://github.com/tommyettinger/colorful-gdx and https://github.com/rkalla/imgscalr/

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
