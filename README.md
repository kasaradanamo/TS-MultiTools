# Multi Tools

![multitools](https://cdn.modrinth.com/data/cached_images/64d706bf853af231988708ef39513e5a1d66f5e0.png)

- **<a href="https://x.com/kasaradanamo" target="_blank">X(Twitter)</a>**<br>
- **<a href="https://github.com/kasaradanamo/TS-MultiTools" target="_blank">GitHub</a>**<br>

---

日本語の説明は下にあります。<br>
<br>
This mod adds multiple multitools.<br>
This Mod is an addon for <a href="https://modrinth.com/project/tokorotenslime" target="_blank">TokorotenSlime</a>, so you'll also need to download that as well.<br>

## Wooden, Stone, Copper, Iron, Golden, Diamond, and Netherite Multitools
### Crafting
- Craft it using a shovel, pickaxe, axe, hoe, and sword of the same material.<br>
  ![multitools_craft](https://cdn.modrinth.com/data/cached_images/8b6c72d68eab09fabadd83c6f476389eda941229.png)

### Specifications
- When attacking an entity, it works as a sword.
- When breaking blocks, it works as a shovel, pickaxe, axe, hoe, or sword depending on the block.
- Right-click to use it as a shovel, axe, or hoe.
- Right-click while holding Shift to use it as a hoe.
- Enchantments for shovels, pickaxes, axes, hoes, and swords can be applied.

### Notes
- Enchantments are not carried over when crafting.
- A Netherite Multitool cannot be crafted from a Diamond Multitool.

Everything up to this point is just a bonus.

## Slime Tool
### Crafting
- It can be crafted with either of the following recipes:
    1. Netherite Shovel, Pickaxe, Axe, Hoe, Sword, Bow, Nether Star, and 2 Slimeballs
    2. Netherite Multitool, Bow, Nether Star, and 2 Slimeballs<br>
       ![slime_recipe_1](https://cdn.modrinth.com/data/cached_images/bf2c031ee913e8095fab396047fefc09bda2eb5d.png)
       ![slime_recipe_2](https://cdn.modrinth.com/data/cached_images/72c83f2d446a00b6aa86ed8354d78fae8ba30b47.png)
       <br><br>
- Enchantments on the ingredients are carried over when crafting.

### Basic Specifications
- It can be used as a multitool and a bow.
- Glass and wool are also treated as suitable blocks.
- It has no durability and consumes experience points when used.
- Using it increases its "Proficiency".
    - The higher the proficiency, the lower the chance of consuming experience points.
    - At the maximum proficiency of 50,000, experience consumption is reduced to 1/5.
    - `/slimecount` sets the count to any value.
- With 0 experience points, it behaves like an item with no functions.<br>(Suitable tool detection and the sword's sweep attack no longer apply.)
- Blocks that break instantly, such as torches, do not consume experience points or increase proficiency.
- Its appearance changes depending on the block being broken and your experience points.
- While holding a torch in your off-hand, placing the torch takes priority where it can be placed.

### Modes
- **X key (default):** Switch the mining mode (Fortune ↔ Silk Touch).
    - The default levels are Fortune III and Silk Touch I.
    - Available when the item has either Fortune or Silk Touch.
- **Ctrl + X key (default):** Switch the use mode (Bow ↔ Tool)
    - **Bow Mode:** Right-click to use it as a bow.
    - **Tool Mode:** Right-click to use it the same way as the multitools.

### Bow Mode
- Arrow damage changes with the attack damage (v1.3.0 and later).
- Draw speed changes with the attack speed (v1.3.0 and later).
- Neither arrow damage nor draw speed is ever lower than a normal bow.
- No arrows are consumed, and fired arrows can only be picked up in Creative mode.

### Enchantments
- Enchantments for the multitools and bows can be applied.
- Unbreaking, Mending, and Infinity cannot be applied.
- Enchantments are easier to get from an Enchanting Table.

### Inheritance (v1.3.0 and later)
- Using a Smithing Table, the Slime Tool can inherit stats from other swords, pickaxes, and other tools.
- **Sword:** If either the attack damage or attack speed is higher than the current Slime Tool, it inherits the sword's attack damage, attack speed, and enchantments (those for swords).
- **Tools (shovel, pickaxe, axe, hoe):** If the mining speed is higher than the current Slime Tool, it inherits the tool's mining speed and enchantments (those for pickaxes).
- **Bow:** It inherits the bow's enchantments (those for bows).
- Stats cannot be inherited if the values are the same.
- Enchantments are grouped into sword, tool, and bow types. Only the enchantments of the inherited type are replaced with the material's, and the other types remain.
- Placing a Slimeball in the left slot inherits only the base stats from the material.<br>(The Slime Tool's enchantments stay the same.)
- Curses are not inherited, and curses on the Slime Tool are not removed.

### Notes
- The X key conflicts with Load Hotbar Activator, so you may need to change one of them.

---

![slime_craft_enchant_en](https://cdn.modrinth.com/data/cached_images/7f71f0fbe99935cf1ef666d084e6a60c57a1fcac.png)

![slime_inheritance_en](https://cdn.modrinth.com/data/cached_images/ce0e3068d9795508353e1112eb33ece1736eb7ff.png)

![slime_inheritance_slimeball_en](https://cdn.modrinth.com/data/cached_images/2637263fa9135501064d59835332b705d828cdff.png)

---

---

The English explanation is above.<br>
<br>
このModは複数のマルチツールを追加します。<br>
このModは<a href="https://modrinth.com/project/tokorotenslime" target="_blank">Tokorotenslime</a>のアドオンであるため、そちらもダウンロードが必要です。<br>

## 木、石、銅、鉄、金、ダイヤ、ネザライトのマルチツール
### クラフト
- それぞれの種類のシャベル、ツルハシ、斧、クワ、剣をクラフトに使用します。<br>
  ![multitools_craft](https://cdn.modrinth.com/data/cached_images/8b6c72d68eab09fabadd83c6f476389eda941229.png)

### 仕様
- エンティティを攻撃する際は剣として動作します。
- ブロックを破壊する際は、対応するブロックに応じてシャベル、ツルハシ、斧、クワ、剣として扱われます。
- 右クリックでシャベル、斧、クワの動作を行えます。
- Shiftキーを押しながら右クリックをすると、クワの動作になります。
- シャベル、ツルハシ、斧、クワ、剣に対応するエンチャントを付与可能です。

### 注意事項
- クラフトする際、エンチャントは引き継げません。
- ダイヤマルチツールから、ネザライトマルチツールをクラフトすることはできません。

ここまではおまけ要素です。

## スライムツール
### クラフト
- 次のいずれかのレシピで作成できます。
    1. ネザライトのシャベル、ツルハシ、斧、クワ、剣、弓、ネザースター、スライムボール×2
    2. ネザライトのマルチツール、弓、ネザースター、スライムボール×2<br>
       ![slime_recipe_1](https://cdn.modrinth.com/data/cached_images/bf2c031ee913e8095fab396047fefc09bda2eb5d.png)
       ![slime_recipe_2](https://cdn.modrinth.com/data/cached_images/72c83f2d446a00b6aa86ed8354d78fae8ba30b47.png)
       <br><br>
- また、クラフト時にエンチャントを引き継ぐことが可能です。

### 基本仕様
- マルチツール+弓の機能を使えます。
- ガラスや羊毛も適性ツールとして扱われます。
- 耐久値がなく、使うと経験値を消費します。
- 使うことで「練度」が増加します。
    - 練度が上がると、経験値の消費確率が減少します。
    - 最大練度50,000で、使用時の経験値消費は1/5になります。
    - `/slimecount`でカウントを任意の値にできます。
- 経験値が0の場合、機能を持たないアイテムを持っている状態と同じになります。<br>(適性ツールの判定や、剣の薙ぎ払いが適用されなくなります)
- 松明など即時破壊できるブロックでは、経験値を消費せず練度も増加しません。
- 壊すブロックや経験値量に応じて、アイテムの見た目が変化します。
- 左手に松明を持っている場合、松明を置ける場所では設置が優先されます。

### モード仕様
- **Xキー(デフォルト):** 採掘モードの切り替え (幸運 ↔ シルクタッチ)。
    - 初期値は幸運Lv3・シルクタッチLv1です。
    - 幸運・シルクタッチのどちらかがエンチャントで付いている場合使用可能です。
- **Xキー(デフォルト) + Ctrlキー:** 使用モード変更 (弓 ↔ ツール)
    - **弓モード:** 右クリックで弓の動作をします。
    - **ツールモード:** 右クリックでマルチツールと同じ動作をします。

### 弓モード仕様
- 攻撃力によって矢の威力が変化します(v1.3.0以降)。
- 攻撃速度によって弓を引く速度が変化します(v1.3.0以降)。
- 威力と速度は通常の弓より値が低くなることはありません。
- 矢の消費はせず、発射された矢はクリエイティブモードでのみ回収可能です。

### エンチャント仕様
- マルチツールや弓に対応するエンチャントを付与可能です。
- 耐久力、修繕、無限のエンチャントは付与できません。
- エンチャントテーブルでの付与時、エンチャントが付きやすくなってます。

### 継承仕様（v1.3.0以降）
- 鍛冶台を使用することで、他の剣やツルハシなどのツールからステータスを継承することができます。
- **剣:** 攻撃力か攻撃速度のどちらかが現在のスライムより高い場合、素材の剣の攻撃力・攻撃速度・エンチャント(剣に対応するもの)を継承します。
- **ツール(シャベル・ツルハシ・斧・クワ):** 採掘速度が現在のスライムより高い場合、素材の採掘速度・エンチャント(ツルハシに対応するもの)を継承します。
- **弓:** エンチャント(弓に対応するもの)を継承します。
- 基礎ステータスが同じ値の場合継承できません。
- エンチャントは、剣・ツール・弓の種類ごとに分かれており、継承した種類のエンチャントだけが素材のものに入れ替わり、他の種類のものは残ります。
- 左のスロットにスライムボールを置くことで、素材の基礎ステータスのみ継承できます。<br>(エンチャントはスライムのままとなります)
- 呪いは継承されず、スライムに付いている呪いも外れません。

### 注意事項
- Xキーはホットバーの読み込みと被っているため、どちらかの変更が必要な場合があります。

---

![slime_craft_enchant_jp](https://cdn.modrinth.com/data/cached_images/e64a8f8949f3c9aeabf32df9de90471b75235bd0.png)

![slime_inheritance_jp](https://cdn.modrinth.com/data/cached_images/12316bc09c8a24d4cf656c468176c2d9bd37d2b8.png)

![slime_inheritance_slimeball_jp](https://cdn.modrinth.com/data/cached_images/8b540e0c5114b2f7d01dc71955ed723db2d69130.png)