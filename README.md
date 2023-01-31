# EUI (Compendium Filters + STSEffekseer)

This mod adds the following features:

- An in-game top panel button that opens a card pool screen with a grid of the cards currently available in your run. You may view upgrades for these cards and show/hide the available colorless cards.
- A card filters menu for the aforementioned grid as well as the card compendium and the master deck screen. You have the option of filtering cards by the mod they came from, their attributes (i.e. card type, rarity, and cost), and their keywords. You will also be shown the number of cards matching the given filters for each keyword. This menu can be accessed by clicking on the "Filters" button at the top-right part of the screen on said pages.
- An overhaul of the card compendium screen that makes the sorting header and color buttons always visible even when you scroll through the compendium (This can be disabled in the mod's settings).
- A variant of the filters menu for the relic compendium, as well as an in-game page on the card pool screen to view relics in a run.
- A mod settings menu that can be accessed mid-run. It can be configured to show all mod settings or to only show certain mod settings.
- Settings to change the fonts used by this mod and the base game. Font changes may be applied universally or only to certain text types. 

Both the card pool and the card filters components come with hooks that allow modders to render custom components within those screens. The card filter also allows modders to define their own custom filters for their custom cards.

This mod also comes with a number of tools for modders:
- Support for playing Effekseer animations in Slay the Spire.
- Support for colorizing Effekseer animations and images in general with the colorful-gdx library, which offers a greater degree of freedom with color manipulation.
- Support for setting up your own fonts.
- Wrappers for quickly deploying and keeping track of ImGUI components.

You can find Effekseer, along with some sample animations, at https://effekseer.github.io/en/.

If you would like to request features or give feedback on this mod, you can do so in this Discord server: https://discord.gg/he76RmsuYZ

## **Requirements**
- BaseMod (5.37.1+)
- ModTheSpire (3.2.2+)
- STSLib (2.0.0+)

Effekseer effects will currently only run on Windows and Linux machines. Macs will require me to build a DLL library specifically for Macs, but I don't have a Mac to do this :(

## **Credits**
- Code adapted from https://github.com/EatYourBeetS/STS-AnimatorMod. Discord link: https://discord.gg/SmHMmJR
- Code adapted from https://github.com/SrJohnathan/gdx-effekseer
- DLL Libraries created from https://github.com/effekseer/EffekseerForMultiLanguages
- Uses functions from https://github.com/tommyettinger/colorful-gdx and https://github.com/rkalla/imgscalr/

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
