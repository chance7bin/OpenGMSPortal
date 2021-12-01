//将一些通用数据保存全局变量，减少不同页面的冗余，使用时引用此文件
var classificationTree_modelItem =   [ //分类树数据
    {"children": [{
            "children": [{
                "id": 2,
                "label": "Land regions",
                "oid": "a24cba2b-9ce1-44de-ac68-8ec36a535d0e"
            }, {"id": 3, "label": "Ocean regions", "oid": "75aee2b7-b39a-4cd0-9223-3b7ce755e457"}, {
                "id": 4,
                "label": "Frozen regions",
                "oid": "1bf4f381-6bd8-4716-91ab-5a56e51bd2f9"
            }, {"id": 5, "label": "Atmospheric regions", "oid": "8f4d4fca-4d09-49b4-b6f7-5021bc57d0e5"}, {
                "id": 6,
                "label": "Space earth",
                "oid": "d33a1ebe-b2f5-4ed3-9c76-78cfb61c23ee"
            }, {"id": 7, "label": "Solid earth", "oid": "d3ba6e0b-78ec-4fe8-9985-4d5708f28e3e"}, {
                "id": 8,
                "label": "Integrated perspective",
                "oid": "eb1d8ddc-6be1-41ef-bab6-a8d940d46499"
            }], "id": 1, "label": "Natural-perspective", "oid": "6b2c8632-964a-4a65-a6c5-c360b2b515f0"
        }, {
            "children": [{
                "id": 10,
                "label": "Administrative regions",
                "oid": "808e74a4-41c6-4558-a850-4daec1f199df"
            }, {"id": 11, "label": "Social regions", "oid": "40534cf8-039a-4a0a-8db9-7c9bff484190"}, {
                "id": 12,
                "label": "Economic regions",
                "oid": "cf9cd106-b873-4a8a-9336-dd72398fc769"
            }, {"id": 13, "label": "Integrated perspective", "oid": "65dbe5a9-ada9-4c02-8353-5029a84d7628"}],
            "id": 9,
            "label": "Human-perspective",
            "oid": "77e7482c-1844-4bc3-ae37-cb09b61572da"
        }], "id": 24, "label": "Application-focused categories", "oid": "9f7816be-c6e3-44b6-addf-98251e3d2e19"},
    {"children": [{
            "children": [{
                "id": 15,
                "label": "Geoinformation analysis",
                "oid": "afa99af9-4224-4fac-a81f-47a7fb663dba"
            }, {
                "id": 16,
                "label": "Remote sensing analysis",
                "oid": "f20411a5-2f55-4ee9-9590-c2ec826b8bd5"
            }, {
                "id": 17,
                "label": "Geostatistical analysis",
                "oid": "1c876281-a032-4575-8eba-f1a8fb4560d8"
            }, {"id": 18, "label": "Machine Learning analysis", "oid": "c6fcc899-8ca4-4269-a21e-a39d38c034a6"}],
            "id": 14,
            "label": "Data-perspective",
            "oid": "4785308f-b2ef-4193-a74b-b9fe025cbc5e"
        }, {
            "children": [{
                "id": 20,
                "label": "Physical process calculation",
                "oid": "1d564d0f-51c6-40ca-bd75-3f9489ccf1d6"
            }, {
                "id": 21,
                "label": "Chemical process calculation",
                "oid": "63266a14-d7f9-44cb-8204-c877eaddcaa1"
            }, {
                "id": 22,
                "label": "Biological process calculation",
                "oid": "6d1efa2c-830d-4546-b759-c66806c4facc"
            }, {"id": 23, "label": "Human-activity calculation", "oid": "6952d5b2-cb0f-4ba7-96fd-5761dd566344"}],
            "id": 19,
            "label": "Process-perspective",
            "oid": "746887cf-d490-4080-9754-1dc389986cf2"
        }], "id": 25, "label": "Method-focused categories", "oid": "5f74872a-196c-4889-a7b8-9c9b04e30718"}];

var languageList = [
    { value: 'af', label: 'Afrikaans' },
    { value: 'sq', label: 'Albanian' },
    { value: 'ar', label: 'Arabic' },
    { value: 'hy', label: 'Armenian' },
    { value: 'az', label: 'Azeri' },
    { value: 'eu', label: 'Basque' },
    { value: 'be', label: 'Belarusian' },
    { value: 'bg', label: 'Bulgarian' },
    { value: 'ca', label: 'Catalan' },
    { value: 'zh', label: 'Chinese' },
    { value: 'hr', label: 'Croatian' },
    { value: 'cs', label: 'Czech' },
    { value: 'da', label: 'Danish' },
    { value: 'dv', label: 'Divehi' },
    { value: 'nl', label: 'Dutch' },
    { value: 'en', label: 'English' },
    { value: 'eo', label: 'Esperanto' },
    { value: 'et', label: 'Estonian' },
    { value: 'mk', label: 'FYRO Macedonian' },
    { value: 'fo', label: 'Faroese' },
    { value: 'fa', label: 'Farsi' },
    { value: 'fi', label: 'Finnish' },
    { value: 'fr', label: 'French' },
    { value: 'gl', label: 'Galician' },
    { value: 'ka', label: 'Georgian' },
    { value: 'de', label: 'German' },
    { value: 'el', label: 'Greek' },
    { value: 'gu', label: 'Gujarati' },
    { value: 'he', label: 'Hebrew' },
    { value: 'hi', label: 'Hindi' },
    { value: 'hu', label: 'Hungarian' },
    { value: 'is', label: 'Icelandic' },
    { value: 'id', label: 'Indonesian' },
    { value: 'it', label: 'Italian' },
    { value: 'ja', label: 'Japanese' },
    { value: 'kn', label: 'Kannada' },
    { value: 'kk', label: 'Kazakh' },
    { value: 'kok', label: 'Konkani' },
    { value: 'ko', label: 'Korean' },
    { value: 'ky', label: 'Kyrgyz' },
    { value: 'lv', label: 'Latvian' },
    { value: 'lt', label: 'Lithuanian' },
    { value: 'ms', label: 'Malay' },
    { value: 'mt', label: 'Maltese' },
    { value: 'mi', label: 'Maori' },
    { value: 'mr', label: 'Marathi' },
    { value: 'mn', label: 'Mongolian' },
    { value: 'ns', label: 'Northern Sotho' },
    { value: 'nb', label: 'Norwegian' },
    { value: 'ps', label: 'Pashto' },
    { value: 'pl', label: 'Polish' },
    { value: 'pt', label: 'Portuguese' },
    { value: 'pa', label: 'Punjabi' },
    { value: 'qu', label: 'Quechua' },
    { value: 'ro', label: 'Romanian' },
    { value: 'ru', label: 'Russian' },
    { value: 'se', label: 'Sami' },
    { value: 'sa', label: 'Sanskrit' },
    { value: 'sr', label: 'Serbian' },
    { value: 'sk', label: 'Slovak' },
    { value: 'sl', label: 'Slovenian' },
    { value: 'es', label: 'Spanish' },
    { value: 'sw', label: 'Swahili' },
    { value: 'sv', label: 'Swedish' },
    { value: 'syr', label: 'Syriac' },
    { value: 'tl', label: 'Tagalog' },
    { value: 'ta', label: 'Tamil' },
    { value: 'tt', label: 'Tatar' },
    { value: 'te', label: 'Telugu' },
    { value: 'th', label: 'Thai' },
    { value: 'ts', label: 'Tsonga' },
    { value: 'tn', label: 'Tswana' },
    { value: 'tr', label: 'Turkish' },
    { value: 'uk', label: 'Ukrainian' },
    { value: 'ur', label: 'Urdu' },
    { value: 'uz', label: 'Uzbek' },
    { value: 'vi', label: 'Vietnamese' },
    { value: 'cy', label: 'Welsh' },
    { value: 'xh', label: 'Xhosa' },
    { value: 'zu', label: 'Zulu' },
];