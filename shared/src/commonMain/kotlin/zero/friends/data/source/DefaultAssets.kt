package zero.friends.data.source

val DEFAULT_RULE_JSON = """
[
  {
    "name": "점당",
    "isEssential": true,
    "script": "필수 항목입니다."
  },
  {
    "name": "뻑",
    "isEssential": false,
    "script": ""
  },
  {
    "name": "첫 따닥",
    "isEssential": false,
    "script": ""
  },
  {
    "name": "광팔기",
    "isEssential": false,
    "script": "4인 플레이 시에만 적용됩니다."
  }
]
""".trimIndent()

val DEFAULT_MANUAL_JSON = """
[
  {
    "title": "옵션 점수",
    "type": "text",
    "script": [
      {
        "header": "뻑",
        "body": "첫 뻑 : 첫 턴에 뻑을 저지른 경우 설정금액 즉시 획득. \n\n연 뻑 : 첫 뻑을 저지른 다음 턴에 다시 뻑을 저지른 경우 설정 금액의 2배 즉시 획득.\n\n삼연뻑 : 연뻑을 저지른 다음 턴에 다시 뻑을 저지른 경우. 설정금액의 4배 즉시 획득하며 승리로 게임이 끝난다."
      },
      {
        "header": "첫 따닥",
        "body": "첫 턴에 따닥을 성공시킨 경우. 점수에는 계산되지 않으나 3점에 해당하는 금액을 게임 도중에 즉시 획득."
      }
    ]
  },
  {
    "title": "승자 점수",
    "type": "text",
    "script": [
      {
        "header": "고",
        "body": "1고를 하면 +1점, 2고를 하면 +2점, 3고 이상 : (본인의 점수에 1점씩 추가하면서) 동시에 x2배씩. 즉 5고는 (5점 추가에) x8배, 6고는 (6점 추가에) x16배"
      },
      {
        "header": "흔들기 & 폭탄",
        "body": "같은 월의 패를 3장 손에 들고 있는 경우, 이를 다른 사람들에게 보여줄 수 있으며 이를 흔들기라 한다. 흔들기를 하거나 폭탄을 던진 사람이 승리할 경우 그의 점수가 2배가 된다."
      },
      {
        "header": "역고",
        "body": "고를 부른 상대방보다 먼저 점수를 낸 다음 역으로 고를 부를 경우 이를 역고라 한다. 역고를 한 이후 스톱하게 되면 점수가 두배로 계산되고 독박까지 쓰게 할수 있다."
      },
      {
        "header": "나가리",
        "body": "마지막 패까지 돌았을 때, 아무도 3점(맞고 7점)을 내지 못하거나, 먼저 고를 선언한 참여자가 추가 점수를 내지 못해서 스톱을 선언하지 못한 경우 나가리가 성립된다. 나가리가 날 경우, 선이 유지되고 게임을 처음부터 다시 시작한다. 단 다음 게임의 점수는 2배로 계산한다."
      }
    ]
  },
  {
    "title": "패자 점수",
    "type": "text",
    "script": [
      {
        "header": "피박",
        "body": "승자가 피 10장 이상을 모아 점수를 얻었는데 피가 5장 이하인 경우. 최종 점수 2배 적용"
      },
      {
        "header": "광박",
        "body": "승자가 광 3장 이상을 모아 점수를 얻었는데 광이 1장도 없는 경우. 최종 점수 2배 적용"
      },
      {
        "header": "멍박",
        "body": "승자가 열끗 으로 점수를 얻었는데 열끗이 1장도 없는 경우. 최종 점수 2배 적용"
      },
      {
        "header": "멍따",
        "body": "열끗이 7장 이상모아서 이긴 경우. 최종 점수 2배 적용"
      },
      {
        "header": "고박",
        "body": "고를 했는데 다른 플레이어가 점수를 모아서 스톱을 선언한 경우 (광팔이 제외)\n맞고 플레이 : 고박 플레이어는 2배의 비용을 지불한다.\n고스톱 플레이 : 고박 플레이어는 다른 패자의 비용까지 대신 지불한다."
      }
    ]
  },
  {
    "title": "화투점수 계산",
    "type": "image",
    "script": [
      {
        "header": "5광•15점",
        "body": ""
      },
      {
        "header": "4광•4점",
        "body": ""
      },
      {
        "header": "3광•3점",
        "body": ""
      },
      {
        "header": "비광•2점",
        "body": "광 2개 + 비광"
      },
      {
        "header": "5장•1점",
        "body": "1장 추가 시 1점씩 추가/고도리 중복계산 가능"
      },
      {
        "header": "5장•1점",
        "body": "1장 추가 시 1점씩 추가\n/홍단,청단,초단 중복계산 가능 "
      },
      {
        "header": "홍단•3점",
        "body": ""
      },
      {
        "header": "청단•3점",
        "body": ""
      },
      {
        "header": "초단•3점",
        "body": ""
      },
      {
        "header": "고도리•5점",
        "body": ""
      },
      {
        "header": "10장•1점",
        "body": "1장 추가 시 1점씩 추가\n/ 쌍피 2점, 쓰리피 3점추가"
      }
    ]
  },
  {
    "title": "점수 2배 만들기",
    "type": "text",
    "script": [
      {
        "header": "피박",
        "body": "피로 1점 이상 점수가 났을 때 상대방의 피가 5장 이하인 경우"
      },
      {
        "header": "광박",
        "body": "광으로 점수가 났을 때 상대방이 광이 한 장도 없는 경우"
      },
      {
        "header": "고박",
        "body": "고를 했는데 다른 플레이어가 점수를 모아서 스톱을 선언한 경우 \n맞고일 경우 고박 플레이어는 2배의 비용을 지불한다."
      },
      {
        "header": "멍박",
        "body": "승자가 열끗 으로 점수를 얻었는데 열끗이 1장도 없는 경우. 최종 점수 2배 적용"
      },
      {
        "header": "흔들기",
        "body": "같은 패가 자기 수중에 3장이 있을 때 승리한 경우\n1고를 하면 +1점, 2고를 하면 +2점, 3고를 하면 점수의 x2점, 4고를 하면 점수의 x4점이 됩니다. \n‘고’를 하고 다른 사람이 3점이 되어 점수가 난다면, 고박을 쓰게 되어 역전이 됩니다."
      },
      {
        "header": "폭탄",
        "body": "흔들 수 있는 상황인데 바닥에 같은 월의 나머지 한 장이 깔려 있는 경우, 3장을 한꺼번에 내려놓았을 경우"
      }
    ]
  },
  {
    "title": "게임진행 방법",
    "type": "text",
    "script": [
      {
        "header": "2명 게임을 플레이 할 경우",
        "body": "2인은 목표 점수가 7점이며, 8장을 판에 깔고 10장씩 가져갑니다."
      },
      {
        "header": "3명 게임을 플레이 할 경우",
        "body": "3인은 목표 점수가 3점이며 6장을 깔고 7장씩 가져갑니다."
      },
      {
        "header": "4명 게임을 플레이 할 경우",
        "body": "4인은 목표 점수가 3점이며 6장을 깔고 7장씩 가져갑니다.\n1명은 반드시 광을 팔고 게임에서 제외됩니다."
      },
      {
        "header": "게임 진행 방법",
        "body": "게임을 주도하는 선이 존재하며, 게임은 항상 선부터 시작하게 된다.\n\n1. 첫 게임에서는 먼저 선을 정한다. 화투장을 뒷면이 보이게 펼쳐놓아 한장씩 뽑거나, 기리를 떼는 방식으로 그 화투장의 월 수로 선을 정한다.\n\n2. 선은 패를 정리하고 잘 섞은 후, 자신의 좌측에 앉은 사람에게 적당한 양의 패를 떼어 더미를 쌓도록 한다. 이 때 한 장도 떼지 않는 경우에는 패를 손가락으로 톡 치면서 \"퉁\"이라고 말한다.\n\n3. 정상적으로 패를 돌렸으면 각 사람은 손에 패를 들고 있으며, 바닥에는 화투가 깔려 있다. 어떤 방법으로 돌려도 큰 상관이 없으나, 가장 일반적인 방법으로는 반시계방향으로 4장씩 돌린 후, 바닥에 3장을 패가 보이도록 내려놓는다. 이후 반시계방향으로 3장씩 돌리고, 다시 3장을 깐다. 퉁인 경우, 한꺼번에 깐다. 퉁이 아닌 경우 돌리다가 패가 모자라면 더미 위에서 가져오고, 패가 남으면 더미 위에 올린다.\n맞고 : 4장씩 2번을 돌리고 5장씩 두번을 깐다.\n\n\n4. 선부터 반시계방향으로 돌면서, 손에 들고 있는 패를 바닥에 내려놓고, 쌓여 있는 더미의 맨 위에서 한장을 뒤집어 바닥에 내려놓는다. 내려놓는 패와 같은 월의 패가 바닥에 깔려있다면 그 패를 먹을 수 있으며, 먹은 패는 점수 룰에 따라 점수를 계산한다.\n\n5. 어느 한 사람이 3점 이상(맞고 : 7점)이 되면 \"났다\"고 하며, 그 사람은 게임을 계속할지, 아니면 중단할지를 결정할 수 있다. 게임을 계속하려면 \"고\"를, 중단하려면 \"스톱\"을 부르면 된다. 스톱하면 그 시점에서 게임을 승리하며, 패자는 최종점수에 해당하는 돈을 승자에게 지불하고, 승자는 다음 게임의 선이 된다.\n\n6. 고를 하면 게임은 계속된다. 고를 부른 사람이 다시 한번 추가 점수 획득에 성공할 경우 보너스를 획득하며(추가 점수, 또는 점수 두 배 등) 다시 한번 고/스톱을 결정할 수 있는 권리를 얻는다. 뻑이나 따닥, 쓸, 쪽 등으로 피를 뺏겨서 점수가 깎인 경우, 예전 점수를 회복한 후 추가점수를 내야 한다. 고를 부르지 못한 사람이 먼저 3점을 만들어서 나게 되면 그 사람이 고/스톱을 결정할 권리를 얻는다. 스톱을 할 경우 맨 처음 고를 부른 사람은 '독박' 또는 '고박'이라고 하여 나머지 패자가 낼 금액까지 새로운 승자에게 모두 물어주어야 하는 페널티를 받는다. 쉽게 말해 역전패.\n\n점수 계산. 게임이 끝나면 더하는 점수를 먼저 더하고, 곱하는 배율을 나중에 곱하여 그에 해당하는 금액을 승자에게 지불한다. "
      }
    ]
  },
  {
    "title": "카드 월별 모음",
    "type": "full_image",
    "script": [
      {
        "header": "",
        "body": ""
      }
    ]
  }
]
""".trimIndent()

val DEFAULT_MANUAL_EN_JSON = """
[
  {
    "title": "Option Score",
    "type": "text",
    "script": [
      {
        "header": "Ppeok",
        "body": "First Ppeok : Immediately earn the configured amount when making a Ppeok on your first turn.\n\nDouble Ppeok : Immediately earn 2x the configured amount when making another Ppeok on the second turn.\n\nTriple Ppeok : Making a third consecutive Ppeok immediately awards 4x the configured amount and ends the game in victory."
      },
      {
        "header": "First Ddadak",
        "body": "Succeeding in a Ddadak on the first turn immediately awards an amount equivalent to 3 points during the game."
      }
    ]
  },
  {
    "title": "Winner Score",
    "type": "text",
    "script": [
      {
        "header": "Go",
        "body": "1 Go adds +1 pt, 2 Go adds +2 pts, 3+ Go adds +1 pt per Go and doubles (x2) for each Go from 3 Go onward (e.g., 5 Go = +5 pts and x8, 6 Go = +6 pts and x16)."
      },
      {
        "header": "Shake & Bomb",
        "body": "Holding 3 cards of the same month allows you to reveal them (Shake) or place all 3 on a matching floor card (Bomb). Winning after Shake or Bomb doubles (x2) your score."
      },
      {
        "header": "Counter Go",
        "body": "Scoring and calling Go after an opponent has already called Go. Stopping after a Counter Go doubles your score and can inflict Go-bak."
      },
      {
        "header": "Nagari",
        "body": "If no player reaches 3 points (7 in 2-player Matgo) by the last card, or a player who called Go fails to score an extra point, the round is declared Nagari and the next game is played at 2x score."
      }
    ]
  },
  {
    "title": "Loser Penalty",
    "type": "text",
    "script": [
      {
        "header": "Pi-bak",
        "body": "When the winner scores with 10+ Pi (junk) cards and a loser has 5 or fewer Pi cards. Doubles (x2) final score."
      },
      {
        "header": "Gwang-bak",
        "body": "When the winner scores with 3+ Gwang (bright) cards and a loser has 0 Gwang cards. Doubles (x2) final score."
      },
      {
        "header": "Meong-bak",
        "body": "When the winner scores with Animal (10-pt) cards and a loser has 0 Animal cards. Doubles (x2) final score."
      },
      {
        "header": "Meong-dda",
        "body": "Winning with 7 or more Animal (10-pt) cards. Doubles (x2) final score."
      },
      {
        "header": "Go-bak",
        "body": "When a player calls Go but another player reaches the target score and declares Stop.\n2-player (Matgo): The Go-bak player pays 2x.\n3+ players (Go-Stop): The Go-bak player pays on behalf of the other losers as well."
      }
    ]
  },
  {
    "title": "Card Scoring",
    "type": "image",
    "script": [
      {
        "header": "5 Brights • 15 pts",
        "body": ""
      },
      {
        "header": "4 Brights • 4 pts",
        "body": ""
      },
      {
        "header": "3 Brights • 3 pts",
        "body": ""
      },
      {
        "header": "Rain Bright • 2 pts",
        "body": "2 Brights + Rain Bright"
      },
      {
        "header": "5 Animals • 1 pt",
        "body": "+1 pt per extra card / Stacks with Godori"
      },
      {
        "header": "5 Ribbons • 1 pt",
        "body": "+1 pt per extra card\n/ Stacks with Red, Blue, Grass Ribbons"
      },
      {
        "header": "Red Ribbons • 3 pts",
        "body": ""
      },
      {
        "header": "Blue Ribbons • 3 pts",
        "body": ""
      },
      {
        "header": "Grass Ribbons • 3 pts",
        "body": ""
      },
      {
        "header": "Godori • 5 pts",
        "body": ""
      },
      {
        "header": "10 Junk (Pi) • 1 pt",
        "body": "+1 pt per extra card\n/ Double Pi counts as 2, Triple Pi as 3"
      }
    ]
  },
  {
    "title": "Score x2 Rules",
    "type": "text",
    "script": [
      {
        "header": "Pi-bak",
        "body": "Winner scores at least 1 pt from Pi and opponent has 5 or fewer Pi cards."
      },
      {
        "header": "Gwang-bak",
        "body": "Winner scores from Gwang and opponent has 0 Gwang cards."
      },
      {
        "header": "Go-bak",
        "body": "A player calls Go, but another player reaches the target score and stops."
      },
      {
        "header": "Meong-bak",
        "body": "Winner scores from Animal cards and opponent has 0 Animal cards."
      },
      {
        "header": "Shake",
        "body": "Winning after revealing 3 cards of the same month in hand."
      },
      {
        "header": "Bomb",
        "body": "Placing 3 cards of the same month from hand onto the 4th matching card on the floor."
      }
    ]
  },
  {
    "title": "How to Play",
    "type": "text",
    "script": [
      {
        "header": "2 Players (Matgo)",
        "body": "Target score is 7 pts. Deal 8 cards to the floor and 10 cards to each player."
      },
      {
        "header": "3 Players",
        "body": "Target score is 3 pts. Deal 6 cards to the floor and 7 cards to each player."
      },
      {
        "header": "4 Players",
        "body": "Target score is 3 pts. Deal 6 cards to the floor and 7 cards to each player.\nOne player sells cards and sits out the round."
      },
      {
        "header": "Game Flow",
        "body": "1. Determine the dealer (First player) by drawing cards.\n\n2. Shuffle and deal cards counterclockwise.\n\n3. On your turn, play a card from your hand to match a floor card of the same month, then flip the top card of the draw pile.\n\n4. When a player reaches 3+ points (7+ in 2-player), they choose to call 'Go' (continue for higher points/multipliers) or 'Stop' (claim victory and settle scores)."
      }
    ]
  },
  {
    "title": "Cards by Month",
    "type": "full_image",
    "script": [
      {
        "header": "",
        "body": ""
      }
    ]
  }
]
""".trimIndent()

val DEFAULT_MANUAL_JA_JSON = """
[
  {
    "title": "オプション点数",
    "type": "text",
    "script": [
      {
        "header": "プッ (Ppeok)",
        "body": "初プッ : 最初のターンにプッをした場合、設定金額を即時獲得。\n\n連プッ : 次のターンに続けてプッをした場合、設定金額の2倍を即時獲得。\n\n三連プッ : 3ターン連続でプッをした場合、設定金額の4倍を即時獲得し勝利となります。"
      },
      {
        "header": "初タダク",
        "body": "最初のターンにタダクを成功させた場合、3点分の金額を即時獲得します。"
      }
    ]
  },
  {
    "title": "勝者点数",
    "type": "text",
    "script": [
      {
        "header": "ゴー (Go)",
        "body": "1ゴーは+1点、2ゴーは+2点、3ゴー以上は1点追加と同時に2倍ずつ乗算されます（5ゴーは+5点＆8倍、6ゴーは+6点＆16倍）。"
      },
      {
        "header": "揺らし＆爆弾",
        "body": "同じ月の札を3枚持っている場合、公開（揺らし）または場の1枚に3枚同時に出す（爆弾）ことができ、勝利時に点数が2倍になります。"
      },
      {
        "header": "逆ゴー",
        "body": "ゴーを宣言した相手より先に点数を出して逆にゴーを宣言すること。ストップすると点数が2倍になります。"
      },
      {
        "header": "ナガリ (流局)",
        "body": "最後の札まで誰も規定点数（3点、2人戦は7点）に達しないか、ゴー宣言者が追加点を出せなかった場合は流局となり、次のゲームの点数が2倍になります。"
      }
    ]
  },
  {
    "title": "敗者ペナルティ",
    "type": "text",
    "script": [
      {
        "header": "ピバク",
        "body": "勝者がカス(ピ)10枚以上で得点し、敗者のカスが5枚以下の場合、最終点数が2倍になります。"
      },
      {
        "header": "光バク",
        "body": "勝者が光(クァン)3枚以上で得点し、敗者の光が0枚の場合、最終点数が2倍になります。"
      },
      {
        "header": "モンバク",
        "body": "勝者がタネ(動物)で得点し、敗者のタネが0枚の場合、最終点数が2倍になります。"
      },
      {
        "header": "モンタ",
        "body": "タネ(動物)を7枚以上集めて勝利した場合、最終点数が2倍になります。"
      },
      {
        "header": "ゴーバク",
        "body": "ゴーを宣言した後に他のプレイヤーがストップして勝利した場合。\n2人戦：2倍を支払います。\n3人以上：他の敗者の分もまとめて支払います。"
      }
    ]
  },
  {
    "title": "花札の点数計算",
    "type": "image",
    "script": [
      {
        "header": "五光 • 15点",
        "body": ""
      },
      {
        "header": "四光 • 4点",
        "body": ""
      },
      {
        "header": "三光 • 3点",
        "body": ""
      },
      {
        "header": "雨三光 • 2点",
        "body": "光2枚 + 雨光"
      },
      {
        "header": "タネ5枚 • 1点",
        "body": "1枚追加ごとに+1点 / ゴドリと重複計算可"
      },
      {
        "header": "短冊5枚 • 1点",
        "body": "1枚追加ごとに+1点\n/ 赤短・青短・草短と重複計算可"
      },
      {
        "header": "赤短 • 3点",
        "body": ""
      },
      {
        "header": "青短 • 3点",
        "body": ""
      },
      {
        "header": "草短 • 3点",
        "body": ""
      },
      {
        "header": "ゴドリ • 5点",
        "body": ""
      },
      {
        "header": "カス10枚 • 1点",
        "body": "1枚追加ごとに+1点\n/ 双ピは2枚分、スリーピは3枚分"
      }
    ]
  },
  {
    "title": "点数2倍ルール",
    "type": "text",
    "script": [
      {
        "header": "ピバク",
        "body": "カスで1点以上獲得した時、相手のカスが5枚以下の場合"
      },
      {
        "header": "光バク",
        "body": "光で得点した時、相手の光が1枚もない場合"
      },
      {
        "header": "ゴーバク",
        "body": "ゴーを宣言したが、他のプレイヤーが規定点に達してストップした場合"
      },
      {
        "header": "モンバク",
        "body": "タネで得点した時、相手のタネが1枚もない場合"
      },
      {
        "header": "揺らし",
        "body": "手札に同じ月の札が3枚ある状態で勝利した場合"
      },
      {
        "header": "爆弾",
        "body": "場に同じ月の札が1枚あり、手札の3枚を同時に出して獲得した場合"
      }
    ]
  },
  {
    "title": "ゲームの進め方",
    "type": "text",
    "script": [
      {
        "header": "2人プレイの場合",
        "body": "目標点数は7点。場に8枚を並べ、各自10枚ずつ手札を持ちます。"
      },
      {
        "header": "3人プレイの場合",
        "body": "目標点数は3点。場に6枚を並べ、各自7枚ずつ手札を持ちます。"
      },
      {
        "header": "4人プレイの場合",
        "body": "目標点数は3点。場に6枚を並べ、各自7枚ずつ手札を持ちます。\n1人は光売りをしてゲームから抜けます。"
      },
      {
        "header": "進行手順",
        "body": "1. 最初のゲームでは親（先手）を決めます。\n\n2. 親が札を混ぜて反時計回りに配ります。\n\n3. 手番では手札から1枚出し、山札の上から1枚めくります。同じ月の札があれば獲得できます。\n\n4. 3点以上（2人戦は7点以上）に達したら、「ゴー」（続行）か「ストップ」（勝利確定・精算）を選択します。"
      }
    ]
  },
  {
    "title": "月別札一覧",
    "type": "full_image",
    "script": [
      {
        "header": "",
        "body": ""
      }
    ]
  }
]
""".trimIndent()
