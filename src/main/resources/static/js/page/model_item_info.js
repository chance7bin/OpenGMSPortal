var info=new Vue({
    el: '#app',
    props:['htmlJson'],
    components: {
        'avatar': VueAvatar.Avatar,
    },
    data: function () {
        return {
            runTemplatesValueIndex: [0, 0],
            inOutParam: [0],

            nowLanguage: '',

            activeNames: 1,
            isCreator: false,
            htmlJSON:{
                "HowtoCite": "How to Cite",

                "CopyrightAndDisclaimer":"Copyright and Disclaimer",
                "CopyrightAndDisclaimerInfo": "All copyrights of a material (model, data, article, etc.) in the OpenGMS fully belong to its author/developer/designer (or any other wording about the owner). The OpenGMS takes every care to avoid copyright infringement, contributor(s) should carefully employ materials from other sources and give proper citations.",
                "Contributor":"Contributor",
                "ContributorInfo": "Contributor is the person who contributes the item to OpenGMS, may not be the author of the item.",

                "Authorship": "Authorship",
                "AuthorshipInfo": "We deeply understand that a model may be attributed to the efforts of a team. If all author information is not listed here, please click the feedback below.",
                "QRCode": "QR Code",
                "graph":"graph",
                "RelateModelItem":"Relate Model Item",
                "Copy":"Copy",
                "Invoke":"Invoke",
                "Resource":"Resource",
                "MDLDescription":"MDL Description",

                "AttributeSet":"AttributeSet",
                "Behavior":"Behavior",
                "Runtime":"Runtime",
                "Style":"Style",
                "Categories":"Categories",
                "Language":"Language",
                "Name":"Name",
                "Keywords":"Keywords",
                "Abstract":"Abstract",
                "Wiki":"Wiki",
                "HardwareConfigures":"HardwareConfigures",
                "SoftwareConfigures":"SoftwareConfigures",
                "Assemblies":"Assemblies",
                "SupportiveResources":"SupportiveResources",
                "RelatedModelItem":"Related Model Item",
                "GroundWaterFlowSystem":"Ground Water Flow System",
                "Type":"Type",
                "Property":"Property",
                "Value":"Value",
                "ShowDetail":"Show Detail",

                "ModelDescription":"Model Description",
                "ModelContentService":"Model Content & Service",
                "RelatedModelsData":"Related Models & Data",
                "RelatedKnowledge":"Related Knowledge",
                "KnowledgeGraph":"Knowledge Graph",
                "CoContributor":"Co-contributor(s)",
                "History":"History",
                "Lastmodifier":"Last modifier",
                "Lastmodifytime":"Last modify time",
                "Modifytimes":"Modify times",
                "ComputableModelList":"Computable Model List",
                "ConceptualschematicModelList":"Conceptual-schematic Model List",
                "LogicalschematicModelList":"Logical-schematic Model List",
                "RelatedLogicalModel":" Edit Related Logical Model",
                "anotherVersion":"There is another version haven't been reviewed, you can edit it later or contact us.",
                "RelatedModelItem":"Edit Related Model Item",
                "Reference":"Reference",
                "Material":"Material",
                "relatedMaterial":"Edit Related Material",
                "relatedReference":"Edit Related Reference",
                "authorshipSubmitted":"These authorship information will be submitted to the contributor to review.",
                "RelatedComputableModel":"Edit Related Computable Model",
                "noComputableModel":"There is no related computable model. You can link computable models.",
                "GraphView":"Graph View",
                "ModelItem":"Model Item",
                "DataItem":"Data Item",
                "RelatedDataItem":"Edit Related Data Item",
                "noRelatedReference":"There is no related reference. You can link references to it.",
                "noRelatedData":"There is no related data item. You can link data items.",
                "NoRelatedMmaterial":"There is no related material. You can link materials to it.",
                "NoRelatedConceptual":"There is no related conceptual model. You can link conceptual models.",
                "NoRelatedLogical":"There is no related logical model. You can link logical models.",
                "NoRelatedModel":"There is no related model item. You can link model items.",

                "RelatedConceptual":"Edit Related Conceptual Model",
                "viewHistory":"viewHistory",

                "Affiliation":"Affiliation",
                "Email":"Email",
                "Homepage":"Homepage",

                "SelectData": "Select Data",
                "UploadData": "Upload Data",
                "AddFolder": "Add folder",
                "DownloadAll": "Download All",
                "Paste": "Paste",
                "DeleteAll": "Delete all",
                "filenamePlaceHolder": "file name...",
                "YouHaveSelected": "You have selected",
                "path": "path",
                "noData": "no data...",
                "Max": "Max",
                "Download": "Download",
                "Copy": "Copy",
                "Share": "Share",
                "Rename": "Rename",
                "rightClick": "Left or right click can call out some function",
                "DatasetName": "Dataset name",
                "StoragePath": "Storage path",
                "UploadFiles": "Upload files",
                "Change": "Change",
                "SelectFile": "Select File",
                "SelectFolder": "Select Folder",
                "AddNewFolder": "Add new folder",
                "deleteInfo": "All of the content will be deleted.",
                "AreYouSureTo": "Are you sure to ",
                "continue": "continue",
                "Back": "Back",
                "allDeleteInfo": "All of the selected files will be deleted.",
                "selectDirTip": "Please select a file directory.",
                "ViewInUserspace": "View in userspace",
                "PleaseSelectDataFirst": "Please select data first"
            },
            authorshipFormVisible: false,
            authorshipForm:{
                name:'',
                ins:'',
                email:'',
                homepage:'',
                oid:'',
            },
            formLabelWidth: '120px',

            dialogTableVisible: false,
            relatedResourceVisible:false,

            relateSearch: "",
            relateType: "",
            typeName: "",
            relateTitle: "",
            tableMaxHeight: 400,
            tableData: [{
                relation:"Connected with",
            }],

            //详情描述语言
            currentDetailLanguage:"",
            detailLanguageList:[],
            detail:"",

            modelReferences: [],

            pageOption_my: {
                paginationShow: false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 5,
                relateSearch: "",
                sortField:"default",
                total: 99999,
                searchResult: [],
            },

            pageOption_all: {
                paginationShow: false,
                progressBar: true,
                sortAsc: false,
                currentPage: 1,
                pageSize: 5,
                relateSearch: "",
                sortField:"viewCount",
                total: 99999,
                searchResult: [],
            },


            activeIndex: '2',
            activeName: 'Computable Model',
            activeName1: 'Model Item',
            activeName2: 'Reference',
            activeName_dialog :"",
            activeRelatedDataName: 'Add Data Items',
            refTableData: [{
                title: 'Anisotropic magnetotransport and exotic longitudinal linear magnetoresistance in WT e2 crystals',
                authors: 'Zhao Y.,Liu H.,Yan J.,An W.,Liu J.,Zhang X.,Wang H.,Liu Y.,Jiang H.,Li Q.,Wang Y.,Li X.-Z.,Mandrus D.,Xie X.~C.,Pan M.,Wang J.',
                date: 'jul 2015',
                journal: 'prb',
                pageRange: "041104"
            }, {
                title: 'Detection of a Flow Induced Magnetic Field Eigenmode in the Riga Dynamo Facility',
                authors: 'Gailitis A.,Lielausis O.,Dement\'ev S.,Platacis E.,Cifersons A.,Gerbeth G.,Gundrum T.,Stefani F.,Christen M.,Hanel H.,Will G.',
                date: 'may 2000',
                journal: 'Physical Review Letters',
                pageRange: "4365-4368"
            }],

            user:{
                email: "",
                accessId: "",
                name: "",
                avatar: "",
            },

            loading: false,
            related3Models: [],
            value1: '1',
            relatedModelNotNull: false,
            relatedModelIsNull: false,
            searchRelatedModelsDialogVisible: false,
            addRelatedModelsDialogVisible: false,
            allRelatedModels: [],
            dataNums: 5,
            timer: false,
            nomore: "",
            nomoreflag: false,

            relatedModelsSearchText: '',
            addModelsSearchText: '',
            searchAddRelatedModels: [],
            searchAddModelPage: 1,

            selectedModels: [],
            selectedModelsOid: [],

            options: [{
                // label: 'Basic',
                options: [{
                    value: 'Connected with'
                }, {
                    value: 'Similar to'
                }, {
                    value: 'Coexist in'
                }]
            }, {
                // label: 'Child',
                options: [{
                    value: 'Evolved from'
                }, {
                    value: 'Belongs to'
                }, {
                    value: 'Integrated into'
                }]
            }, {
                // label: 'Parent',
                options: [{
                    value: 'Inspires'
                }, {
                    value: 'Contains'
                }, {
                    value: 'Employs/Depends on'
                }]
            }],

            modelOid:'',
            editModelItemDialog:false,

            //editClassification
            treeData: [
                {
                    id: 1,
                    label: 'Earth System Subject',
                    oid: 'fc236e9d-3ae9-4594-b9b8-de0ac336a1d7',
                    children: [ {
                        id: 65,
                        label: 'Solar-terrestrial Physics',
                        oid: '1fd56a5d-1532-4ea6-ad0a-226e78a12861'
                    }, {
                        id: 66,
                        label: 'Earth Surface System',
                        oid: '4f162f21-2375-468e-90af-d3267d0ba05f',
                        children: [{
                            id: 2,
                            label: 'Hydrosphere',
                            oid: '652bf1f8-2f3e-4f93-b0dc-f66505090873'
                        }, {
                            id: 3,
                            label: 'Lithosphere',
                            oid: 'a621ea24-26d5-4027-a8de-d418509dacb2'
                        }, {
                            id: 4,
                            label: 'Atmosphere',
                            oid: '5e324fc8-93d1-40bb-a2e4-24d2dff68c4b'
                        }, {
                            id: 5,
                            label: 'Biosphere',
                            oid: '76cb072d-8f56-4e34-9ea6-1a95ea7f474b'
                        }, {
                            id: 6,
                            label: 'Anthroposphere',
                            oid: 'eccbe4e1-32f6-490e-9bf7-ae774be472ac'

                        }, {
                            id: 7,
                            label: 'Synthesis',
                            oid: '1a59f012-0659-479d-a183-b74921c67a08'
                        }]
                    },{
                        id: 67,
                        label: 'Solid Earth Geophysics',
                        oid: '52e69d15-cc83-43fb-a445-0c15e5f46878'
                    },]
                },{
                    id: 64,
                    label: 'Geography Subject',
                    oid: 'd7824a16-0f3a-4186-8cb7-41eb10028177',
                    children: [{
                        id: 8,
                        label: 'Physical Geography',
                        oid: '44068d3f-533a-4567-9bfd-07eea9d9e8af',
                        children: [{
                            id: 9,
                            label: 'Hydrology',
                            oid: '158690be-1a1d-4e09-86a5-cbd5c0104206'
                        }, {
                            id: 10,
                            label: 'Geomorphology',
                            oid: '17b746ad-7dcf-4aa5-90b5-104c041caf62'
                        }, {
                            id: 11,
                            label: 'Geology',/////
                            oid: '19bff3af-4c8d-4d98-9ad0-18e34a818a50'
                        }, {
                            id: 12,
                            label: 'Glaciology',
                            oid: 'cfc349aa-63dc-498a-a9e0-6867bad3a2a6'
                        }, {
                            id: 13,
                            label: 'Biogeography',
                            oid: '7656e180-c975-47fe-8ea6-abf417a94793'
                        }, {
                            id: 14,
                            label: 'Meteorology',
                            oid: 'e3e1e879-ce41-46a5-b72c-55501bb08ce8'
                        }, {
                            id: 15,
                            label: 'Climatology',
                            oid: 'dcb2fa01-5507-4fbd-a533-1b7336cd497b'
                        }, {
                            id: 16,
                            label: 'Pedology',
                            oid: '40d18155-6669-4416-990c-de0374ab587e'
                        }, {
                            id: 17,
                            label: 'Oceanography',
                            oid: 'ea1f9c14-9bdb-4da6-b728-a9853620e95f'
                        }, {
                            id: 18,
                            label: 'Coastal Geography',
                            oid: '12b11f3e-8d6e-48c9-bf3a-f9fb5c5e0dd4'
                        }, {
                            id: 19,
                            label: 'Landscape Ecology',
                            oid: '00190eef-017f-42b3-8500-baf612083557'
                        }, {
                            id: 20,
                            label: 'Ecosystem',
                            oid: '60d4f9cf-df22-4313-8b53-c7c314455f2d'
                        }, {
                            id: 21,
                            label: 'Paleogeography',
                            oid: '6965468a-f952-4adf-87e9-6dc2988ab7f8'
                        }, {
                            id: 22,
                            label: 'Quaternary Science',
                            oid: '9de1a9a7-4f84-4f8d-9ee6-3aaa33681e29'
                        }, {
                            id: 23,
                            label: 'Environmental Management',
                            oid: '5d8d6338-0624-40dd-8519-ec440b47c174'
                        }, {
                            id: 24,
                            label: 'Global Synthesis',
                            oid: 'a0c97d7a-54c6-4bbe-8e6d-9fe9b2234a1e'
                        }, {
                            id: 25,
                            label: 'Regional Synthesis',/////
                            oid: 'aacf6bc4-8280-4f75-919d-3e4be604dd88'
                        }, {
                            id: 26,
                            label: 'Others',
                            oid: 'f69d3040-abad-477d-9194-b6ee5303bd9a'
                        }]
                    }, {
                        id: 27,
                        label: 'Human Geography',
                        oid: '3a76212e-c4f2-4a99-ab98-51ae5e7cf7e0',
                        children: [{
                            id: 28,
                            label: 'Agricultural Geography',
                            oid: '7cf1aa10-58c0-4329-9a1d-9ace0cc2ba33'
                        }, {
                            id: 29,
                            label: 'Industrial Geography',
                            oid: 'e9590d02-c1bf-4f92-878c-4f2857fc9c33'
                        }, {
                            id: 30,
                            label: 'Traffic Geography',
                            oid: '64eb0340-6312-4549-9671-6bd635d5a8b3'
                        }, {
                            id: 31,
                            label: 'Tourism Geography',
                            oid: 'bfa6147d-700e-4e06-978e-c9f0266608a8'
                        }, {
                            id: 32,
                            label: 'Population Geography',/////
                            oid: 'a9fc055b-99a1-40c9-82de-626de69efc04'
                        }, {
                            id: 33,
                            label: 'Regional Geography',
                            oid: '0be6cd3b-a459-45df-b7e7-b2fb23aafd12'
                        }, {
                            id: 34,
                            label: 'Urban Geography',
                            oid: '51574401-09d9-4819-aa3e-17994e0396fd'
                        }, {
                            id: 35,
                            label: 'Rural Geography',/////
                            oid: 'b0cc3872-2c89-428a-ac50-7d30f7638373'
                        }, {
                            id: 36,
                            label: 'Historical Geography',
                            oid: '9efcb0d7-9374-4fa4-b1c3-8a9409320813'
                        }, {
                            id: 37,
                            label: 'Cultural Geography',
                            oid: '13e811de-f061-432b-9ed4-85bda9d385c7'
                        }, {
                            id: 38,
                            label: 'Social Geography',
                            oid: 'dfb2fc17-f084-4e6b-ae89-ef35f4563be3'
                        }, {
                            id: 39,
                            label: 'Economic Geography',
                            oid: '6d4b41d2-6922-4642-bfe4-235a55002f67'
                        }, {
                            id: 40,
                            label: 'Political Geography',
                            oid: '7a5fdbe5-ac48-45ea-a56a-29ff10e32789'
                        }, {
                            id: 41,
                            label: 'Health Geography',
                            oid: '0761b9dc-4324-46f0-a8d5-3516fd6308d9'
                        }, {
                            id: 42,
                            label: 'Development Geography',
                            oid: '671c0a46-fc81-47ed-94c3-af12c696156b'
                        }, {
                            id: 43,
                            label: 'Behavioral Geography',
                            oid: 'f25d4aa8-3adf-47fa-8b8d-adf885e7c5aa'
                        }, {
                            id: 44,
                            label: 'Global Synthesis',
                            oid: 'd4ceefe8-0c2b-4ea1-af1d-a7e0f3c7218c'
                        }, {
                            id: 45,
                            label: 'Regional Synthesis',/////
                            oid: 'ea50ad38-0b15-49b4-a183-676ba7487446'
                        }, {
                            id: 46,
                            label: 'Others',
                            oid: 'ba898bbd-1902-44ae-ac3f-0cc5bc944bc5'
                        }]
                    }, {
                        id: 47,
                        label: 'GIScience & Remote Sensing',
                        oid: '3afc51dc-930d-4ab5-8a59-3e057b7eb086',
                        children: [{
                            id: 48,
                            "label": "Shape Processing",
                            "oid": "e6984ef1-4f69-4f6e-be2b-c77f917de5a5",
                        },
                            {
                                id: 49,
                                "label": "Grid Processing",
                                "oid": "944d3c82-ddeb-4b02-a56c-44eb419ecc13",
                            },
                            {
                                id: 50,
                                "label": "Imagery Processing",
                                "oid": "5e184a2e-2579-49bf-ebac-7c28b24a38e3",
                            },
                            {
                                id: 51,
                                "label": "Data Management",
                                "oid": "6cc12923-edc1-4faf-8c7d-a14240cd897b",
                            },
                            {
                                id: 52,
                                "label": "Spatial Analysis",
                                "oid": "d7f96d42-b6c5-4984-81f6-6589cff37285",
                            },
                            {
                                id: 53,
                                "label": "Geostatistics",
                                "oid": "f08f8694-1909-4ca2-b943-e8db0c0f5439",
                            },
                            {
                                id: 54,
                                "label": "Terrain Analysis",
                                "oid": "b74f0952-143b-4af7-8fa6-ad9bf4787cb9",
                            },
                            {
                                id: 55,
                                "label": "3D Analyst",
                                "oid": "340c275a-1ed4-495b-8415-a6a4bfe4eb18",
                            },{
                                id: 56,
                                "label": "Network Analysis",
                                "oid": "fa7d7d50-098e-4cd7-92c7-31755b3ca371",
                            },{
                                id: 57,
                                "label": "Geographic Simulation",
                                "oid": "ab1f3806-1ed8-4fd9-ff06-b6c2ca020ae9",
                            },
                            {
                                id: 58,
                                "label": "Climate Tools",
                                "oid": "40b78ccf-e430-4756-84d7-9dfdd9ccfcad"
                            },{
                                id: 59,
                                "label": "Generic Tools",
                                "oid": "77567bff-52b9-4833-885d-417bd3a6c0e9"
                            },{
                                id: 60,
                                label: 'Cartography',
                                oid: '854189a4-3811-441d-a9d1-7de58e57a37f'
                            },
                            {
                                id: 61,
                                label: 'Remote Sensing Imagery',
                                oid: '84e1090a-3f27-43fe-b912-d0dd7e9c8677'
                            }, {
                                id: 62,
                                label: 'Ground Feature Spectrum',
                                oid: '63097163-10e5-4e16-8335-590dcc7156ba'
                            }, {
                                id: 63,
                                label: 'Others',/////
                                oid: '10bef187-00bf-4cea-b192-bf1465a265b1'
                            }]
                    }]}

            ],
            treeData2:[
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
                            "oid": "396cc739-ef33-4332-8d5d-9a67c89567c7"
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
                        }],
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
                    }], "id": 25, "label": "Method-focused categories", "oid": "5f74872a-196c-4889-a7b8-9c9b04e30718"}],
            cls:[],
            clsStr:"",
            cls2:[],
            clsStr2:"",
            editClassification:false,
            defaultProps: {
                children: 'children',
                label: 'label'
            },

            itemInfo:{},

            model_classifications:[],

            relationPageSize:3,

            modelRelationGraphShow:false,
            modelRelationGraphSideBarShow:false,

            relatedModelItems:[],
            relatedModelItemsPage:[],
            curRelation:{},
            graphFullScreen: false,
            graphId: "modelRelationGraph",
            modelRelationGraph: null,

            nodeLabelShow:true,
            fullNameShow:false,
            lineLabelShow:true,
            lineColorShow:false,
            fullRelationShow:false,

            lightenContributor:{},
            contributors:[],

            relateFile: [],

            exLink:{
                name:'',
                content:'',
            },

            targetFile:[],

            showDataChose:false,

            editDescription:false,

            currentLocalization: {
                localCode: "",
                localName: "",
                name: "",
                description: "",
            },
            localIndex: 0,
            languageAdd: {
                show: false,
                local: {

                },
            },
            localizationList: [
                {
                    localCode: "en",
                    localName: "English",
                    name: "",
                    description: "",
                    selected: true,
                }
            ],
            languageList:[
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
            ],

            editReference:false,
            editArticleDialog:false,

            showUploadArticleDialog:false,

            showUploadedArticleDialog:false,

            articleUploading:{
                title:'',
                authors:[],
                journal:'',
                pageRange:'',
                date:2019,
                doi:'',
                status:'',
                link:'',
            },

            doiLoading:false,

            doi:'',

            dynamicTable:{},

            metaDataTab:'first',

            metadata:{
                overview:{
                    name:'',
                    version:'',
                    modelType:'',
                    modelDomain:[],
                    scale:'',
                },
                design:{
                    purpose:'',
                    principles:[],
                    incorporatedModels:[],
                    framework:'',
                    process:[],
                },
                usage:{
                    information:'',
                    initialization:'',
                    hardware:'',
                    software:'',
                    inputs:[],
                    outputs:[],
                }
            },

            editMetadata:false,
            editAliasDialog:false,
            editingModelAlias:'',

            checkUrlValidLoading:false,
            htmlJson:{},

            ModelMetaData:{
                pie: {
                    modelPurpose: [''],
                    modelAssumption: [''],
                    basicPrinciple: [''],
                    modelCategory: undefined,
                    classificationInfo: [''],
                    applicationScope: {
                        timeScope: {
                            resolution: '',
                            scale: '',
                            step: '',
                            scope: ''
                        },
                        spatialScope: {
                            dimension: '',
                            gridType: '',
                            resolution: '',
                            scale: '',
                            scope: ''
                        }
                    },
                    codeLanguage: []
                },
                mp: {
                    modelName: {
                        modelName: '',
                        modelAlias: []
                    },
                    modelVersion: {
                        seriesName: '',
                        numbering: '',
                        purpose: undefined,
                        changeContent: '',
                        author: [
                            {
                                contactorName: {
                                    fullName: '',
                                    surname: ''
                                },
                                contactorAddress: {
                                    country: '',
                                    province: '',
                                    city: '',
                                    address: '',
                                    postCode: ''
                                },
                                title: [''],
                                affiliation: [''],
                                email: [''],
                                phone: [''],
                                fax: [''],
                                personalPage: ['']
                            }
                        ],
                        date: undefined
                    },
                    descriptionInfo: {
                        descAbstract: '',
                        detailDescription: ''
                    },
                    keywords: [],
                    referenceSystem: {
                        temporalReference: {
                            name: ''
                        },
                        spatialReference: [{
                            type: '',
                            name: ''
                        }]
                    },
                    developInfo: {
                        startDate: '',
                        endDate: '',
                        process: '',
                        developer: [
                            {
                                contactorName: {
                                    fullName: '',
                                    surname: ''
                                },
                                contactorAddress: {
                                    country: '',
                                    province: '',
                                    city: '',
                                    address: '',
                                    postCode: ''
                                },
                                title: [''],
                                affiliation: [''],
                                email: [''],
                                phone: [''],
                                fax: [''],
                                personalPage: ['']
                            }
                        ]
                    },
                    publishInfo: {
                        publishMode: {
                            openAccess: '',
                            accessMethod: '',
                            usageMethod: '',
                            openSourceProtocol: ''
                        },
                        publishMethod: {
                            transmitMode: '',
                            accessLocation: ''
                        },
                        publishDate: '',
                        publisher: [
                            {
                                contactorName: {
                                    fullName: '',
                                    surname: ''
                                },
                                contactorAddress: {
                                    country: '',
                                    province: '',
                                    city: '',
                                    address: '',
                                    postCode: ''
                                },
                                title: [''],
                                affiliation: [''],
                                email: [''],
                                phone: [''],
                                fax: [''],
                                personalPage: ['']
                            }
                        ]
                    },
                    relateItem: [{
                        projectName: '',
                        projectId: '',
                        fundingAgency: '',
                        type: ''
                    }],
                    reference: [{
                        title: '',
                        author: '',
                        agency: '',
                        press: '',
                        reel: '',
                        issue: '',
                        page: '',
                        publishDate:'',
                        url: ''
                    }],
                    occupySpace: '',
                    occupySpaceUnit: 'KB',
                    metaDataVersion: {
                        id: '',
                        purpose: '',
                        changeContent: '',
                        author: [
                            {
                                contactorName: {
                                    fullName: '',
                                    surname: ''
                                },
                                contactorAddress: {
                                    country: '',
                                    province: '',
                                    city: '',
                                    address: '',
                                    postCode: ''
                                },
                                title: [''],
                                affiliation: [''],
                                email: [''],
                                phone: [''],
                                fax: [''],
                                personalPage: ['']
                            }
                        ],
                        date: ''
                    }
                },
                mo: {
                    modelStructure: {
                        legend: '',
                        type: ''
                    }
                    ,
                    subModel: [
                        {
                            modelName: '',
                            version: '',
                            versionId: '',
                            usage: ''
                        }
                    ],
                    couplingMode: ''
                },
                di: {
                    formula: '',
                    term: [{
                        name: '',
                        desc: ''
                    }],
                    inOutPara: [
                        {
                            name: '',
                            type: '',
                            desc: '',
                            unit: '',
                            limit: '',
                            defaultValue: '',
                            format: ''
                        }
                    ]
                },
                mdt: {
                    runTime:'',
                    runTemplates:[
                        {
                            configInfo:[
                                {
                                    name: '',
                                    type: '',
                                    value: ''
                                }
                            ],
                            desc: ''
                        }
                    ],
                    modelAnalysis:{
                        modelCalibration: '',
                        modelValidation:'',
                        effectivenessAnalysis:'',
                        uncertaintyAnalysis: '',
                        sensitivityAnalysis: ''
                    },
                    instruction:''
                },
                editTime: '',
                createTime: ''
            },

            ModelMetaDataList: [],

            pie: {
                modelPurpose: [''],
                modelAssumption: [''],
                basicPrinciple: [''],
                classificationInfo: [''],
                modelCategory: undefined,
                applicationScope: {
                    timeScope: {
                        resolution: '',
                        scale: '',
                        step: '',
                        scope: ''
                    },
                    spatialScope: {
                        dimension: '',
                        gridType: '',
                        resolution: '',
                        scale: '',
                        scope: ''
                    }
                },
                codeLanguage: []
            },

            mp: {
                modelName: {
                    modelName: '',
                    modelAlias: []
                },
                modelVersion: {
                    seriesName: '',
                    numbering: '',
                    purpose: undefined,
                    changeContent: '',
                    author: [
                        {
                            contactorName: {
                                fullName: '',
                                surname: ''
                            },
                            contactorAddress: {
                                country: '',
                                province: '',
                                city: '',
                                address: '',
                                postCode: ''
                            },
                            title: [''],
                            affiliation: [''],
                            email: [''],
                            phone: [''],
                            fax: [''],
                            personalPage: ['']
                        }
                    ],
                    date: undefined
                },
                descriptionInfo: {
                    descAbstract: '',
                    detailDescription: ''
                },
                keywords: [],
                referenceSystem: {
                    temporalReference: {
                        name: ''
                    },
                    spatialReference: [{
                        type: '',
                        name: ''
                    }]
                },
                developInfo: {
                    startDate: '',
                    endDate: '',
                    process: '',
                    developer: [
                        {
                            contactorName: {
                                fullName: '',
                                surname: ''
                            },
                            contactorAddress: {
                                country: '',
                                province: '',
                                city: '',
                                address: '',
                                postCode: ''
                            },
                            title: [''],
                            affiliation: [''],
                            email: [''],
                            phone: [''],
                            fax: [''],
                            personalPage: ['']
                        }
                    ]
                },
                publishInfo: {
                    publishMode: {
                        openAccess: '',
                        accessMethod: '',
                        usageMethod: '',
                        openSourceProtocol: ''
                    },
                    publishMethod: {
                        transmitMode: '',
                        accessLocation: ''
                    },
                    publishDate: '',
                    publisher: [
                        {
                            contactorName: {
                                fullName: '',
                                surname: ''
                            },
                            contactorAddress: {
                                country: '',
                                province: '',
                                city: '',
                                address: '',
                                postCode: ''
                            },
                            title: [''],
                            affiliation: [''],
                            email: [''],
                            phone: [''],
                            fax: [''],
                            personalPage: ['']
                        }
                    ]
                },
                relateItem: [{
                    projectName: '',
                    projectId: '',
                    fundingAgency: '',
                    type: ''
                }],
                reference: [{
                    title: '',
                    author: '',
                    agency: '',
                    press: '',
                    reel: '',
                    issue: '',
                    page: '',
                    publishDate:'',
                    url: ''
                }],
                occupySpace: '',
                occupySpaceUnit: 'KB',
                metaDataVersion: {
                    id: '',
                    purpose: '',
                    changeContent: '',
                    author: [
                        {
                            contactorName: {
                                fullName: '',
                                surname: ''
                            },
                            contactorAddress: {
                                country: '',
                                province: '',
                                city: '',
                                address: '',
                                postCode: ''
                            },
                            title: [''],
                            affiliation: [''],
                            email: [''],
                            phone: [''],
                            fax: [''],
                            personalPage: ['']
                        }
                    ],
                    date: ''
                }
            },

            mo: {
                modelStructure: {
                    legend: '',
                    type: ''
                }
                ,
                subModel: [
                    {
                        modelName: '',
                        version: '',
                        versionId: '',
                        usage: ''
                    }
                ],
                couplingMode: ''
            },

            di: {
                formula: '',
                term: [{
                    name: '',
                    desc: ''
                }],
                inOutPara: [
                    {
                        name: '',
                        type: '',
                        desc: '',
                        unit: '',
                        limit: '',
                        defaultValue: '',
                        format: ''
                    }
                ]
            },

            mdt: {
                runTime:'',
                runTemplates:[
                    {
                        configInfo:[
                            {
                                name: '',
                                type: '',
                                value: ''
                            }
                        ],
                        desc: ''
                    }
                ],
                modelAnalysis:{
                    modelCalibration: '',
                    modelValidation:'',
                    effectivenessAnalysis:'',
                    uncertaintyAnalysis: '',
                    sensitivityAnalysis: ''
                },
                instruction:''
            },

            typeOptions:[{
                value:1,
                label:'输入'
            },{
                value:2,
                label:'输出'
            },{
                value:3,
                label:'控制参数'
            }],

            legendOptions:[{
                value:1,
                label: '复杂型(通常用运行结构图例表示)'
            },{
                value:2,
                label: '简单计算型'
            },{
                value:3,
                label: '时间推进型'
            },{
                value:4,
                label: '事件响应型'
            }],

            dialogImageUrl: '',
            dialogVisible: false,

            formData: new FormData(),
            fileList: [],

            modelTypeOptions:[{
                value:1,
                label: '模型'
            },{
                value:2,
                label: '工具'
            },{
                value:3,
                label: '模块'
            },{
                value:4,
                label: '公式'
            }],

            scaleOptions:[{
                value:1,
                label: '百万年以上'
            },{
                value:2,
                label: '百万年尺度'
            },{
                value:3,
                label: '万年尺度'
            },{
                value:4,
                label: '千年尺度'
            },{
                value:5,
                label: '百年尺度'
            },{
                value:6,
                label: '10年尺度'
            },{
                value:7,
                label: '年'
            },{
                value:8,
                label: '月'
            },{
                value:9,
                label: '日'
            },{
                value:10,
                label: '日以下'
            },{
                value:11,
                label: '基于事件(次，如降水)'
            }],

            dimensionOptions:[{
                    value:1,
                    label: '1D'
                },{
                    value:2,
                    label: '1.5D'
                },{
                    value:3,
                    label: '2D'
                },{
                    value:4,
                    label: '2.5D'
                },{
                    value:5,
                    label: '3D'
                }],

            spatialScaleOptions:[{
                value:1,
                label: '全球尺度'
            },{
                value:2,
                label: '洲际尺度'
            },{
                value:3,
                label: '区域尺度'
            },{
                value:4,
                label: '流域尺度'
            },{
                value:5,
                label: '基本单元(地块尺度)'
            }],

            metaPurposeOptions:[{
                value: 1,
                label: '首次创建'
            },{
                value: 2,
                label: '修改'
            }],

            dynamicTags: ['标签一', '标签二', '标签三'],
            inputVisible: false,
            inputVisible2: false,
            inputVisible4: false,
            inputVisible5: false,
            inputVisible6: false,
            inputValue: '',
            inputValue2: '',
            inputValue4: '',
            inputValue5: '',
            inputValue6: '',

            purposeOptions:[{
                value:1,
                label: '首次创建'
            },{
                value:2,
                label: '修改问题'
            },{
                value:3,
                label: '增加新功能'
            },{
                value:4,
                label: '调整'
            }],

            occupySpaceOptions:[{
                value:'KB',
                label: 'KB'
            },{
                value:'MB',
                label: 'MB'
            },{
                value:'GB',
                label: 'GB'
            }],

            authorDialogTableVisible: false,
            developerDialogTableVisible: false,
            publisherDialogTableVisible: false,
            metaDialogTableVisible: false,

            processOptions:[{
                value:1,
                label: '完成'
            },{
                value:2,
                label: '开发中'
            },{
                value:3,
                label: '已计划'
            },{
                value:4,
                label: '有需求'
            },{
                value:5,
                label: '即将完成'
            },{
                value:6,
                label: '历史档案'
            },{
                value:7,
                label: '已作废'
            }],

            openAccessOptions:[{
                value:true,
                label: '开源'
            },{
                value:false,
                label: '不开源'
            }],

            classificationList:[
                { "value": "陆地圈", "address": "陆地圈" },
                { "value": "海洋圈", "address": "海洋圈" },
                { "value": "冰冻圈", "address": "冰冻圈" },
                { "value": "大气圈", "address": "大气圈" },
                { "value": "太空-地球", "address": "太空-地球" },
                { "value": "固体地球", "address": "固体地球" },
                { "value": "发展活动", "address": "发展活动" },
                { "value": "社会活动", "address": "社会活动" },
                { "value": "经济活动", "address": "经济活动" },
                { "value": "全球尺度", "address": "全球尺度" },
                { "value": "区域尺度", "address": "区域尺度" },
                { "value": "地理信息分析", "address": "地理信息分析" },
                { "value": "遥感分析", "address": "遥感分析" },
                { "value": "地学统计分析", "address": "地学统计分析" },
                { "value": "智能分析计算", "address": "智能分析计算" },
                { "value": "物理过程计算", "address": "物理过程计算" },
                { "value": "化学过程计算", "address": "化学过程计算" },
                { "value": "生物过程计算", "address": "生物过程计算" },
                { "value": "人类活动计算", "address": "人类活动计算" }
            ],

            rule1: {
                modeltype: [{ required: true, message: '请选择模型类型', trigger: 'blur' }],
                typeInfo: [{ required: true, message: '请输入分类信息', trigger: 'blur' }],
            },

            rules3: {
                modelName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
            }
        }
    },
    methods: {
        // 获取缓存
        getStorage(key){
            var localStorage = window.localStorage;
            if (localStorage )
                var v = localStorage.getItem(key);
            if (!v) {
                return;
            }
            if (v.indexOf('obj-') === 0) {
                v = v.slice(4);
                return JSON.parse(v);
            } else if (v.indexOf('str-') === 0) {
                return v.slice(4);
            }
        },

        translatePage(jsonContent){
            this.htmlJSON = jsonContent;
            this.nowLanguage = window.localStorage.getItem("language");
            console.log(this.nowLanguage)

            let el_breadcrumb = $(".el-breadcrumb");

            for(i=0;i<el_breadcrumb.length;i++){
                let el_breadcrumb_items = el_breadcrumb.eq(i).children(".el-breadcrumb__item");
                let classification_path = this.model_classifications[i];
                for(j=0;j<el_breadcrumb_items.length;j++){
                    let cls = ''
                    if(jsonContent.History === "History") {//英文页面
                        cls = classification_path[j].name;
                    }else{//汉化页面
                        cls = classification_path[j].name_zh;
                    }
                    el_breadcrumb_items.eq(j).children(".el-breadcrumb__inner")[0].innerText = cls
                }

            }

        },
        generateGUID() {
            var s = [];
            var hexDigits = "0123456789abcdef";
            for (var i = 0; i < 36; i++) {
                s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
            }
            s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
            s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
            s[8] = s[13] = s[18] = s[23] = "-";

            var uuid = s.join("");
            return uuid;
        },

        fullRelationShowChange(newValue){
            this.fullRelationShow = newValue;
            this.generateModelRelationGraph();
        },

        lineColorShowChange(newValue){
            let opts = this.modelRelationGraph.getOption();

            let links = opts.series[0].links;
            for(i = 0;i<links.length;i++){
                let link = links[i];
                let formatter;
                let name = links[i].name;
                if(newValue){
                    let color;
                    switch (links[i].label.formatter){
                        case "Connected with":
                            color="#f6b26b";
                            break;
                        case "Evolved from":
                            color="#ffd966";
                            break;
                        case "Belongs to":
                            color="#93c47d";
                            break;
                        case "Integrated into":
                            color="#6d9eeb";
                            break;
                        case "Inspires":
                            color="#8e7cc3";
                            break;
                        case "Contains":
                            color="#c27ba0";
                            break;
                        case "Employs/Depends on":
                            color="#a61c00";
                            break;
                        case "Similar to":
                            color="#e69138";
                            break;
                        case "Coexist in":
                            color="#7f6000";
                            break;
                    }
                    links[i].lineStyle={
                        color: color,
                    }
                }else{
                    links[i].lineStyle={}
                }
            }
            opts.series[0].links = links;
            this.modelRelationGraph.clear();
            this.modelRelationGraph.setOption(opts);
        },

        lineLabelShowChange(newValue){
            let opts = this.modelRelationGraph.getOption();

            let links = opts.series[0].links;
            for(i = 0;i<links.length;i++){
                let link = links[i];
                let formatter;
                let name = links[i].name;
                links[i].label.show = newValue;

                if(link.label.formatter==undefined){
                    links[i].label.show = false;
                }
            }
            opts.series[0].links = links;
            this.modelRelationGraph.clear();
            this.modelRelationGraph.setOption(opts);
        },

        uuid() {
            var s = [];
            var hexDigits = "0123456789abcdef";
            for (var i = 0; i < 36; i++) {
                s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
            }
            s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
            s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
            s[8] = s[13] = s[18] = s[23] = "-";

            var uuid = s.join("");
            return uuid;
        },

        fullNameShowChange(newValue){
            let opts = this.modelRelationGraph.getOption();

            let data = opts.series[0].data;
            for(i = 0;i<data.length;i++){
                let formatter;
                let name = data[i].name;;
                if(newValue){
                    formatter = name.trim().replaceAll(" ","\n");
                }else{
                    formatter = name.length > 9 ? name.substring(0,7)+"..." : name
                }

                if(data[i].value.type=="ref"){
                    formatter="";
                }

                data[i].label={
                    formatter:formatter
                }
            }
            opts.series[0].data = data;
            this.modelRelationGraph.clear();
            this.modelRelationGraph.setOption(opts);
        },

        nodeLabelShowChange(newValue){
            let opts = this.modelRelationGraph.getOption();
            this.modelRelationGraph.clear();
            opts.series[0].label.show=newValue;
            this.modelRelationGraph.setOption(opts);
        },

        graphFullDialogClose(){
            let opt = this.modelRelationGraph.getOption();

            let object = document.getElementById("modelRelationGraph");
            let modelRelationGraph = echarts.init(object,'light');
            this.modelRelationGraph = modelRelationGraph;
            this.modelRelationGraph.setOption(opt);
        },

        handleRelationCurrentChange(page,type){
            switch(type){
                case "modelItem":
                    let start = (page-1)*this.relationPageSize;
                    let end = page * this.relationPageSize;
                    this.relatedModelItemsPage = [];
                    let relatedModelItems = this.relation.modelItems;
                    for(i=start;i<relatedModelItems.length;i++){
                        if(i===end) break;
                        this.relatedModelItemsPage.push(relatedModelItems[i]);
                    }
                    break;
            }
        },

        closeGraphSideBar(){
            this.modelRelationGraphSideBarShow = false;
        },

        relateModelItemListShowChange(val){
            console.log(val);
            if(val) {
                this.generateModelRelationGraph();
            }else{
                this.closeModelRelationGraph();
            }
        },

        generateModelRelationGraph(){
            this.modelRelationGraphShow = true;

            let nodes = [];
            let links = [];

            $.post("/modelItem/relationGraph",{"id":this.modelId,"isFull":this.fullRelationShow},(result)=>{
                console.log(result);
                nodes = result.data.nodes;
                links = result.data.links;

                Vue.nextTick(()=>{
                    if(this.modelRelationGraph == null) {
                        let object = document.getElementById("modelRelationGraph");
                        let modelRelationGraph = echarts.init(object, 'light');
                        this.modelRelationGraph = modelRelationGraph;
                    }

                    this.modelRelationGraph.clear();
                    this.modelRelationGraph.showLoading();


                    this.modelRelationGraph.on("click",(param)=>{
                        if(param.value !== undefined){
                            this.curRelation=param.value;
                            this.modelRelationGraphSideBarShow = true;
                        }
                        console.log(param)
                    });

                    let graph_nodes = [];
                    let graph_links = [];

                    let radius = 200;
                    let title = this.itemInfo.name;
                    graph_nodes.push({
                        name: title,
                        x: 500,
                        y: 300,
                        value: {
                            style: "node",
                            type: "model",
                            name: nodes[0].name,
                            oid: nodes[0].oid,
                            img: nodes[0].img,
                            overview: nodes[0].overview,
                        },
                        itemStyle:{
                            color: 'green',
                        },
                        label: {
                            formatter: title.length > 9 ? title.substring(0,7)+"..." : title,
                        },
                        tooltip:{
                            formatter:"{b}",
                        },
                    });

                    let dtAngle = 360 / nodes.length;
                    let curAngle = 0;
                    //加入节点
                    for(i = 1;i<nodes.length;i++){
                        let node = nodes[i];

                        curAngle = curAngle + dtAngle;
                        let radian = curAngle * 2 * Math.PI / 360;
                        let dx = Math.cos(radian) * radius;
                        let dy = Math.sin(radian) * radius;

                        let name = node.name;

                        //加入节点
                        if(node.type === "ref"){
                            let formatter = name.length > 9 ? name.substring(0,7)+"..." : name;
                            graph_nodes.push({
                                name: node.name,
                                value: {
                                    style: "node",
                                    type: "ref",
                                    name: node.name,
                                    author: node.author,
                                    journal: node.journal,
                                    link: node.link,
                                },
                                x: graph_nodes[0].x + dx,
                                y: graph_nodes[0].y + dy,
                                symbolSize: 8,
                                itemStyle:{
                                    color: 'skyblue',
                                },
                                label: {
                                    show: false,
                                    formatter: formatter,
                                },
                                tooltip: {
                                    formatter: "Reference: {b}",
                                },
                            });
                        }else {
                            let name = node.name;
                            let start = name.indexOf("(");
                            let end = name.indexOf(")");
                            if(name.length>0&&start!=-1&&end!=-1) {
                                let part1 = name.substring(0, start).trim();
                                if(end + 1 == name.length){
                                    name = part1;
                                }else {
                                    let part2 = name.substring(end + 1, name.length - 1);
                                    name = part1 + " " + part2;
                                }
                            }
                            let formatter = name.length > 9 ? name.substring(0, 7) + "..." : name;
                            graph_nodes.push({
                                name: name,
                                value: {
                                    style: "node",
                                    type: "model",
                                    name: node.name,
                                    oid: node.oid,
                                    img: node.img,
                                    overview: node.overview,
                                },
                                x: graph_nodes[0].x + dx,
                                y: graph_nodes[0].y + dy,
                                // symbolSize: 10,
                                itemStyle:{
                                    color: 'orange',
                                },
                                label: {
                                    formatter: formatter,
                                },
                                tooltip: {
                                    formatter: "{b}",
                                },
                            });
                        }
                    }

                    //加入连线
                    for(i = 0;i<links.length;i++) {
                        let link = links[i];
                        if(link.type === "ref") {
                            graph_links.push({
                                source: link.ori,
                                target: link.tar,
                                // symbolSize: [5, 10],
                                label: {
                                    show: false,
                                    formatter: link.relation,
                                    fontSize: 12,
                                },
                                lineStyle: {
                                    width: 2,
                                    curveness: 0
                                },
                                symbol: ['none', 'none'],
                                tooltip: {
                                    show: false,
                                    position: 'bottom',
                                    formatter: nodes[link.ori].name + " " + link.relation + " " + nodes[link.tar].name,
                                },

                            });
                        }else{
                            graph_links.push({
                                source: link.ori,
                                target: link.tar,
                                symbolSize: [5, 10],
                                label: {
                                    show: true,
                                    formatter: link.relation,
                                    fontSize: 12,
                                },
                                lineStyle: {
                                    width: 2,
                                    curveness: 0
                                },
                                tooltip: {
                                    position: 'bottom',
                                    formatter: nodes[link.ori].name + " " + link.relation + " " + nodes[link.tar].name,
                                },

                            });
                        }
                    }

                    let option = {
                        title: {
                            //text: 'Graph 简单示例'
                        },
                        tooltip: {},
                        toolbox: {
                            right:10,
                            feature: {

                                myFull: {
                                    show: true,
                                    title: 'Full Screen',
                                    icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                                    onclick: (e)=>{
                                        let opts = e.getOption();
                                        opts.toolbox[0].feature.myFull={};//.show=false;
                                        // opts.toolbox[0].feature.myFullExit.show=true;
                                        this.graphFullScreen = true;

                                        Vue.nextTick(()=>{
                                            let object = document.getElementById('fullScreenGraph');
                                            let graph = echarts.init(object,'light');
                                            this.modelRelationGraph = graph;
                                            this.modelRelationGraph.setOption(opts);

                                            this.modelRelationGraph.on("click",(param)=>{
                                                if(param.value !== undefined){
                                                    this.curRelation=param.value;
                                                    this.modelRelationGraphSideBarShow = true;
                                                }

                                                console.log(param)
                                            });

                                            // opts.toolbox[0].feature.myFull.show=false
                                            // //window.top表示最顶层iframe  如果在当页面全屏打开 删去window.top即可
                                            // window.top.layer.open({
                                            //     title:false,
                                            //     type:1,
                                            //     content:'<div class="fullChart" style="height:100%;width:100%;padding:30px 0px"></div>',
                                            //     success:function(){
                                            //         var fullchart = echarts.init(window.top.document.getElementById('fullChart'))
                                            //         fullchart.setOption(opts)
                                            //     }
                                            // })
                                        });

                                    }
                                },
                                saveAsImage: {},
                                restore: {},
                                // myFullExit: {
                                //     show: false,
                                //     title: 'Exit',
                                //     icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                                //     onclick: (e)=>{
                                //         this.graphFullScreen = false;
                                //
                                //     }
                                // },
                            }
                        },
                        animation: false,
                        // animationDurationUpdate: 500,
                        // animationEasingUpdate: 'quinticInOut',
                        series: [
                            {
                                type: 'graph',
                                layout: 'force',
                                draggable: false,
                                focusNodeAdjacency:true,
                                symbolSize: 25,
                                zoom:4,
                                roam: true,
                                force:{
                                    repulsion:100,
                                    // edgeLength:[150,200],
                                    layoutAnimation:false,
                                },
                                label: {
                                    show: true,
                                },
                                edgeSymbol: ['circle', 'arrow'],
                                edgeSymbolSize: [4, 10],
                                edgeLabel: {
                                    fontSize: 20
                                },
                                data: [{
                                    name: '节点1',
                                    x: 500,
                                    y: 300,
                                    symbolSize:50,
                                    itemStyle:{
                                        color: 'blue',
                                    },
                                }, {
                                    name: '节点2',
                                    x: 800,
                                    y: 300
                                }, {
                                    name: '节点3',
                                    x: 550,
                                    y: 100
                                }, {
                                    name: '节点4',
                                    x: 550,
                                    y: 500
                                }],
                                // links: [],
                                links: [{
                                    source: 0,
                                    target: 1,
                                    symbolSize: [5, 20],
                                    label: {
                                        show: true,
                                        formatter:"1234",
                                    },
                                    lineStyle: {
                                        width: 5,
                                        curveness: 0
                                    }
                                }, {
                                    source: '节点2',
                                    target: '节点1',
                                    label: {
                                        show: true
                                    },
                                    lineStyle: {
                                        curveness: 0.2
                                    }
                                }, {
                                    source: '节点1',
                                    target: '节点3'
                                }, {
                                    source: '节点2',
                                    target: '节点3'
                                }, {
                                    source: '节点2',
                                    target: '节点4'
                                }, {
                                    source: '节点1',
                                    target: '节点4'
                                }],
                                lineStyle: {
                                    opacity: 0.9,
                                    width: 2,
                                    curveness: 0
                                }
                            }
                        ]
                    };

                    option.series[0].data = graph_nodes;
                    option.series[0].links = graph_links;
                    console.log(option);
                    this.modelRelationGraph.setOption(option);
                    this.modelRelationGraph.hideLoading();
                })

            });



        },

        closeModelRelationGraph(){
            this.modelRelationGraphShow = false;
            this.modelRelationGraphSideBarShow = false;
            this.modelRelationGraph = null;
        },

        relationSortChange(sort){
            console.log(sort);
            let order = sort.order==="ascending";
            let field = sort.column.label.toLowerCase();
            if(this.activeName_dialog==="my"){
                this.pageOption_my.sortAsc=order;
                this.pageOption_my.sortField=field;
            }else{
                this.pageOption_all.sortAsc=order;
                this.pageOption_all.sortField=field;
            }
            this.search(this.activeName_dialog);
        },

        getModelId(){
            let url = window.location.href;
            let urls = url.split("/");
            for(i=0;i<urls.length;i++){
                if(urls[i].length>=36){
                    return urls[i].substring(0,36);
                }
            }
        },

        changeDetailLanguage(command){
            this.currentDetailLanguage = command;
            let data = {
                "id": this.getModelId(),
                "language": this.currentDetailLanguage
            };

            if(window.location.href.indexOf("history")===-1) {
                $.get("/modelItem/detailByLanguage", data, (result) => {
                    this.detail = result.data;
                })
            }else{
                $.get("/version/languageDetail/modelItem", data, (result) => {
                    this.detail = result.data;
                })
            }
        },

        getAlias(){

            axios.get('/modelItem/alias/'+this.modelId
            ).then(res => {
                if(res.data.code == -1){
                    this.confirmLogin()
                }else{
                    this.editAliasDialog = true
                    Vue.nextTick(()=>{
                        if($('#aliasInput').nextAll().length>0){
                            $('#aliasInput').tagEditor('destroy');
                        }
                        $('#aliasInput').tagEditor({
                            initialTags: res.data.data==null?[]:res.data.data ,
                            forceLowercase: false,
                            // placeholder: 'Enter alias ...'
                        });
                    })
                }


            })
        },

        submitModelAlias(){
            let alias = $("#aliasInput").val().split(",");
            if (alias.length === 1 && alias[0] === "") {
                alias = [];
            }

            let data = {
                id:this.modelId,
                alias:alias,
            };
            $.post("/modelItem/alias",data,(result)=>{
                if (result.code == -1) {
                    this.confirmLogin()
                }else{
                    if(result.data=='suc'){
                        this.$alert("Change classification successfully!", 'Success', {
                            type: 'success',
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.reload();
                            }
                        });
                    }else if(result.data=='version'){
                        this.$alert("Your edit has been submit, please wait for the contributor to handle it.", 'Success', {
                            type: 'success',
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.reload();
                            }
                        })
                    }
                }




            })
        },
        openClassEditDialog(){
            axios.get("/user/load")
                .then((res) => {
                    if (res.data.code !== 0) {
                        this.confirmLogin()
                    }else{
                        this.editClassification = true;
                    }
                })
        },

        getClassifications(){
            $.get("/modelItem/classifications/"+this.modelId,{},(result)=>{
                if (result.code == -1) {
                    this.confirmLogin()
                }else{
                    //cls

                    this.editClassification = true;

                    this.cls = result.data;

                    this.$nextTick(()=>{
                        this.$refs.editClassificationModule.insertClassifications(this.cls)
                    })

                }
            });

        },
        submitClassifications(){
            let data = {
                id:this.modelId,
                // class1:this.cls,
                class:this.cls,
            };
            $.ajax({
                type: "PUT",
                url: "/modelItem/classifications",
                data: data,
                cache: false,
                async: false,
                success: (result) => {
                    if (result.code == -1) {
                        this.confirmLogin()
                    }else{
                        if(result.data=='suc'){
                            this.$alert("Change classification successfully!", 'Success', {
                                type: 'success',
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.reload();
                                }
                            });
                        }else if(result.data=='version'){
                            this.$alert("Your edit has been submit, please wait for the contributor to handle it.", 'Success', {
                                type: 'success',
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.reload();
                                }
                            })
                        }
                    }
                }
            })
        },
        getClassificationFromCom(cls,clsStr){
            this.cls=cls;
            this.clsStr2=clsStr;
        },
        addLocalization() {
            this.languageAdd.show = true;
        },

        //description
        confirmAddLocal() {

            if (this.languageAdd.local.label == undefined) {
                this.$message({
                    message: 'Please selcet a language!',
                    type: 'warning'
                });
                return;
            }
            for (i = 0; i < this.localizationList.length; i++) {
                let localization = this.localizationList[i];
                if (localization.localName == this.languageAdd.local.label) {
                    this.$message({
                        message: 'This language already exists!',
                        type: 'warning'
                    });
                    return;
                }
            }

            let newLocalization = {
                localCode: this.languageAdd.local.value,
                localName: this.languageAdd.local.label,
                name: "",
                description: "",
            };
            this.localizationList.push(newLocalization);
            this.languageAdd.local = {};

            this.changeLocalization(newLocalization);
        },
        cancelAddLocal() {
            this.languageAdd.show = false;
        },
        deleteLocalization(row) {
            this.$confirm('Do you want to delete <b>' + row.localName + '</b> description?', 'Warning', {
                dangerouslyUseHTMLString: true,
                confirmButtonText: 'Yes',
                cancelButtonText: 'No',
                type: 'warning'
            }).then(() => {

                for (i = 0; i < this.localizationList.length; i++) {
                    let localization = this.localizationList[i]
                    if (localization.localName == row.localName) {
                        this.localizationList.splice(i, 1);
                        break;
                    }
                }
                if (this.localizationList.length > 0) {
                    this.changeLocalization(this.localizationList[0]);
                } else {
                    this.changeLocalization(null);
                }

                this.$message({
                    type: 'success',
                    message: 'Delete ' + row.localName + ' successfully!',
                });
            }).catch(() => {

            });

        },
        changeLocalization(row) {
            if (row == null) {
                this.currentLocalization = {
                    localCode: "",
                    localName: "",
                    name: "",
                    description: "",
                };
                tinymce.activeEditor.setContent("");
                // tinymce.undoManager.clear();
            } else {
                for (i = 0; i < this.localizationList.length; i++) {
                    this.$set(this.localizationList[i], "selected", false);
                    if (this.currentLocalization.localName == this.localizationList[i].localName) {
                        this.localizationList[i].name = this.currentLocalization.name;
                        this.localizationList[i].description = tinymce.activeEditor.getContent();
                        break;
                    }
                }
                this.$set(row, "selected", true);
                this.currentLocalization = row;
                tinymce.activeEditor.setContent(row.description);
                // tinymce.undoManager.clear();
            }
        },

        // insertMetaData(metadata,data){
        //     let overview = data.overview
        //     let design = data.design
        //     let usage = data.usage
        //
        //     metadata.overview.name = overview.name
        //     metadata.overview.version = overview.version
        //     metadata.overview.modelType = overview.modelType
        //     metadata.overview.scale = overview.scale
        //
        //     metadata.design.purpose = design.purpose
        //     metadata.design.framework = design.framework
        //
        //     metadata.usage.information = usage.information
        //     metadata.usage.initialization = usage.initialization
        //     metadata.usage.hardware = usage.hardware
        //     metadata.usage.software = usage.software
        //
        //     Vue.nextTick(()=>{
        //         if ($("#modelDomainInput").nextAll().length != 0){
        //             $('#modelDomainInput').tagEditor('destroy');
        //         }
        //         $('#modelDomainInput').tagEditor({
        //             initialTags: overview.modelDomain==null?[]:overview.modelDomain ,
        //             forceLowercase: false,
        //         });
        //         if ($("#principlesInput").nextAll().length != 0){
        //             $('#principlesInput').tagEditor('destroy');
        //         }
        //         $('#principlesInput').tagEditor({
        //             initialTags: design.principles==null?[]:design.principles ,
        //             forceLowercase: false,
        //         });
        //         if ($("#incorporatedModelsInput").nextAll().length != 0){
        //             $('#incorporatedModelsInput').tagEditor('destroy');
        //         }
        //
        //         $('#incorporatedModelsInput').tagEditor({
        //             initialTags: design.incorporatedModels==null?[]:design.incorporatedModels ,
        //             forceLowercase: false,
        //         });
        //         if ($("#processInput").nextAll().length != 0){
        //             $('#processInput').tagEditor('destroy');
        //         }
        //         $('#processInput').tagEditor({
        //             initialTags: design.process==null?[]:design.process ,
        //             forceLowercase: false,
        //         });
        //         if ($("#inputsInput").nextAll().length != 0){
        //             $('#inputsInput').tagEditor('destroy');
        //         }
        //         $('#inputsInput').tagEditor({
        //             initialTags: usage.inputs==null?[]:usage.inputs ,
        //             forceLowercase: false,
        //         });
        //         if ($("#outputsInput").nextAll().length != 0){
        //             $('#outputsInput').tagEditor('destroy');
        //         }
        //         $("#outputsInput").tagEditor({
        //             initialTags: usage.outputs==null?[]:usage.outputs ,
        //             forceLowercase: false,
        //         })
        //     })
        //
        //
        //
        // },

        // getMetaData(){
        //     let metadata = {
        //         'overview':{},
        //         'design':{},
        //         'usage':{},
        //     }
        //     metadata.overview.name = this.metadataTemp.overview.name==null?null:this.metadataTemp.overview.name.trim()
        //     metadata.overview.version = this.metadataTemp.overview.version==null?null:this.metadataTemp.overview.version.trim()
        //     metadata.overview.modelType = this.metadataTemp.overview.modelType==null?null:this.metadataTemp.overview.modelType.trim()
        //     metadata.overview.modelDomain = $("#modelDomainInput").val().split(",");
        //     if (metadata.overview.modelDomain.length === 1 && metadata.overview.modelDomain[0] === "") {
        //         metadata.overview.modelDomain = [];
        //     }
        //     metadata.overview.scale = this.metadataTemp.overview.scale==null?null:this.metadataTemp.overview.scale.trim()
        //
        //     metadata.design.purpose = this.metadataTemp.design.purpose==null?null:this.metadataTemp.design.purpose.trim()
        //     metadata.design.principles = $("#principlesInput").val().split(",");
        //     if (metadata.design.principles.length === 1 && metadata.design.principles[0] === "") {
        //         metadata.design.principles = [];
        //     }
        //     metadata.design.incorporatedModels = $("#incorporatedModelsInput").val().split(",");
        //     if (metadata.design.incorporatedModels.length === 1 && metadata.design.incorporatedModels[0] === "") {
        //         metadata.design.incorporatedModels = [];
        //     }
        //     metadata.design.framework = this.metadataTemp.design.framework==null?null:this.metadataTemp.design.framework.trim()
        //     metadata.design.process = $("#processInput").val().split(",");
        //     if (metadata.design.process.length === 1 && metadata.design.process[0] === "") {
        //         metadata.design.process = [];
        //     }
        //     metadata.usage.information = this.metadataTemp.usage.information==null?null:this.metadataTemp.usage.information.trim()
        //     metadata.usage.initialization = this.metadataTemp.usage.initialization==null?null:this.metadataTemp.usage.initialization.trim()
        //     metadata.usage.hardware = this.metadataTemp.usage.hardware==null?null:this.metadataTemp.usage.hardware.trim()
        //     metadata.usage.software = this.metadataTemp.usage.software==null?null:this.metadataTemp.usage.software.trim()
        //     metadata.usage.inputs = $("#inputsInput").val().split(",");
        //     if (metadata.usage.inputs.length === 1 && metadata.usage.inputs[0] === "") {
        //         metadata.usage.inputs = [];
        //     }
        //     metadata.usage.outputs = $("#outputsInput").val().split(",");
        //     if (metadata.usage.outputs.length === 1 && metadata.usage.outputs[0] === "") {
        //         metadata.usage.outputs = [];
        //     }
        //     return metadata
        // },

        getDescription(){
            axios.get('/modelItem/localizationList/'+this.modelId
            ).then(
                res => {
                    if(res.data.code==-1){
                        this.confirmLogin()
                    }else{
                        this.editDescription = true
                        let data = res.data.data
                        Vue.nextTick(()=>{
                            initTinymce('textarea#conceptText')
                            this.localizationList = data
                            let interval = setInterval(() => {
                                this.changeLocalization(this.localizationList[0])
                                clearInterval(interval);
                            }, 1000);
                        })

                    }

                }
            )
        },

        getMetadata(){
            axios.get('/modelItem/getMetaData/'+this.modelId
            ).then(
                res => {
                    if(res.data.code==-1){
                        this.confirmLogin()
                    }else{
                        if (this.isCreator == 'false'){
                            this.$alert('目前暂不支持编辑他人创建的模型的元数据，请期待后续更新~', '提示', {
                                confirmButtonText: '确定',
                                callback: action => {
                                    let data = res.data.data
                                    // 如果是null，则不操作
                                    if (data == null){
                                        return
                                    }
                                    this.ModelMetaData = {...data}
                                    this.editMetadata = true

                                }
                            })
                        }else {
                            this.editMetadata = true
                            let data = res.data.data
                            // 如果是null，则不操作
                            if (data == null){
                                const uuid = this.uuid();
                                this.ModelMetaData.mp.metaDataVersion.id = uuid;
                                return
                            }
                            this.ModelMetaData = {...data}
                        }
                    }
                }
            )
        },

        // submitMetadata(){
        //     let data = {}
        //     data.oid = this.modelId
        //     data.metadata = JSON.stringify(this.getMetaData())
        //
        //     $.ajax({
        //         type:'post',
        //         url:'/modelItem/updateMetadata',
        //         data: data,
        //         // dataType: "json",
        //         // accept: 'application/json',
        //         success:(res)=>{
        //             let data = res.data
        //             if (res.code == -1) {
        //                 this.confirmLogin()
        //             }else{
        //                 if(data=='suc'){
        //                     this.$alert("Change metadata successfully!", 'Success', {
        //                         type: 'success',
        //                         confirmButtonText: 'OK',
        //                         callback: action => {
        //                             window.location.reload();
        //                         }
        //                     })
        //                 }else if(data=='version'){
        //                     this.$alert("Your edit has been submit, please wait for the contributor to handle it.", 'Success', {
        //                         type: 'success',
        //                         confirmButtonText: 'OK',
        //                         callback: action => {
        //                             window.location.reload();
        //                         }
        //                     })
        //                 }
        //             }
        //
        //
        //         }
        //     })
        // },

        submitDescription(){
            for (i = 0; i < this.localizationList.length; i++) {
                if (this.currentLocalization.localName == this.localizationList[i].localName) {
                    this.localizationList[i].name = this.currentLocalization.name;
                    this.localizationList[i].description = tinymce.activeEditor.getContent();
                    break;
                }
            }

            let data = {
                id:this.modelId,
                localizations:JSON.stringify(this.localizationList)
            }

            $.ajax({
                type:'PUT',
                url:'/modelItem/localizations',
                data: data,
                // dataType: "json",
                // accept: 'application/json',
                success:(res)=>{
                    let data = res.data
                    if (res.code == -1) {
                        this.confirmLogin()
                    }else{
                        if(data=='suc'){
                            this.$alert("Change description successfully!", 'Success', {
                                type: 'success',
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.reload();
                                }
                            })
                        }else if(data=='version'){
                            this.$alert("Your edit has been submit, please wait for the contributor to handle it.", 'Success', {
                                type: 'success',
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.reload();
                                }
                            })
                        }
                    }


                }
            })
            // $.post('/modelItem/updateDesctription',data).then(
            //     (result)=> {
            //
            //     }
            // )
        },

        getReference(){

            axios.get('/modelItem/references/'+this.modelId).then(
                res =>{
                    if (res.data.code == -1) {
                        this.confirmLogin()
                    }else{
                        this.editReference = true

                        let refs = res.data.data

                        if (refs != null) {
                            let jsonArray = [];
                            for (i = 0; i < refs.length; i++) {
                                let json = JSON.parse(refs[i].replaceAll(/\\/g,"").replaceAll(/\"\{/g,"{").replaceAll(/\}\"/g,"}"));
                                json.authors = json.authors.join(", ");
                                jsonArray.push(json);
                            }
                            // console.log(json);
                            this.refTableData = jsonArray;
                        }
                        Vue.nextTick(()=>{
                            if (this.dynamicTable) {
                                this.dynamicTable = $('#dynamic-table').DataTable();
                            }
                            else {
                                this.dynamicTable = $('#dynamic-table').DataTable({
                                    //"aaSorting": [[ 0, "asc" ]],
                                    "paging": false,
                                    // "ordering":false,
                                    "info": false,
                                    "searching": false,
                                });
                            }

                            this.dynamicTable.clear().draw();
                            for (i = 0; i < this.refTableData.length; i++) {
                                var ref = this.refTableData[i];
                                this.dynamicTable.row.add([
                                    ref.title,
                                    ref.authors,
                                    ref.date,
                                    ref.journal,
                                    ref.volume,
                                    ref.pageRange,
                                    ref.link,
                                    ref.doi,
                                    "<center><a href='javascript:;' class='fa fa-times refClose' style='color:red'></a></center>"]).draw();
                            }
                        })
                    }
                }
            )
        },

        searchDoi(){
            if(this.doi == ''){
                this.$alert('Please input the DOI', 'Tip', {
                        type:"warning",
                        confirmButtonText: 'OK',
                        callback: ()=>{
                            return
                        }
                    }
                );
            }else{
                this.doiLoading = true
                // if(this.doi===this.lastDoi)
                //     setTimeout(()=>{
                //         this.showUploadedArticleDialog=true;
                //         this.doiLoading = false;
                //     },200)
                // this.lastDoi=this.doi;
                $.ajax({
                    type: "POST",
                    url: "/modelItem/searchByDOI",
                    data: {
                        doi: this.doi,
                        modelOid:this.modelId
                    },
                    cache: false,
                    async: true,
                    success: (res) => {
                        if(res.code==-1) {
                            this.$alert('Please login first!', 'Error', {
                                type:"error",
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.href = "/user/login";
                                }
                            });
                        }
                        let data=res.data;
                        this.doiLoading = false;
                        if (data.find == -1) {
                            this.$alert('Failed to connect, please try again!', 'Tip', {
                                    type:"warning",
                                    confirmButtonText: 'OK',
                                    callback: ()=>{
                                        return
                                    }
                                }
                            );
                        }else if(data.find==0){
                            this.$alert('Find no result, check the DOI you have input or fill information manually.', 'Tip', {
                                    type:"warning",
                                    confirmButtonText: 'OK',
                                    callback: ()=>{
                                        return
                                    }
                                }
                            );
                        }
                        else if(data.find==1) {

                            this.showUploadArticleDialog = true;
                            this.articleUploading = data.article;

                        }else if(data.find==2){
                            this.showUploadedArticleDialog=true;

                        }

                    },
                    error: (data) => {
                        this.doiLoading = false;
                        $("#doi_searchBox").removeClass("spinner")
                        this.$alert('Failed to connect, please try again!', 'Tip', {
                                type:"warning",
                                confirmButtonText: 'OK',
                                callback: ()=>{
                                    return
                                }
                            }
                        );
                        $("#doiDetails").css("display", "none");
                        $("#doiTitle").val("")
                    }
                })
            }
        },

        updateArticleConfirmClick(){
            // console.log(this.articleToBack);
            var tags = $('#refAuthor').tagEditor('getTags')[0].tags;
            for (i = 0; i < tags.length; i++) { $('#articleAuthor').tagEditor('removeTag', tags[i]); }
            if(tags.length<1||$("#refTitle").val()==''){
                this.$alert('Please enter the Title and at least one Author.', 'Tip', {
                        type:"warning",
                        confirmButtonText: 'OK',
                        callback: ()=>{
                            return
                        }
                    }
                );
                return;
            }

            let tags1 = $('#refAuthor').tagEditor('getTags')[0].tags;
            for (i = 0; i < tags1.length; i++) { $('#refAuthor').tagEditor('removeTag', tags1[i]); }
            if (tags1.length>0&&$("#refTitle").val()!='') {
                this.dynamicTable.row.add([
                    $("#refTitle").val(),
                    tags1,
                    $("#refDate").val(),
                    $("#refJournal").val(),
                    $("#volumeIssue").val(),
                    $("#refPages").val(),
                    $("#refLink").val(),
                    $("#doiTitle").val(),
                    "<center><a href='javascript:;' class='fa fa-times refClose' style='color:red'></a></center>"]).draw();

                $("#dynamic-table").css("display", "block")
                $("#refinfo").modal("hide")
                $("#refTitle").val("")
                var tags = $('#refAuthor').tagEditor('getTags')[0].tags;
                for (i = 0; i < tags.length; i++) {
                    $('#refAuthor').tagEditor('removeTag', tags[i]);
                }
                $("#refDate").val("")
                $("#volumeIssue").val("")
                $("#refJournal").val("")
                $("#refPages").val("")
                $("#doiTitle").val("")
                $("#refLink").val("")
            }

            this.editArticleDialog = false
            //调用$("#modal_save").click完成

        },

        cancelSearch() {
            this.editArticleDialog = false
        },

        articleDoiUploadConfirm(status) {
            this.articleToBack = this.articleUploading;

            Vue.nextTick(()=>{
                $("#refTitle").val(this.articleToBack.title);
                $("#refJournal").val(this.articleToBack.journal);
                $("#volumeIssue").val(this.articleToBack.volume);
                $("#refPages").val(this.articleToBack.pageRange);
                $("#refDate").val(this.articleToBack.date);
                $("#refLink").val(this.articleToBack.link);
                if ($("#refAuthor").nextAll().length == 0) {//如果不存在tageditor,则创建一个
                    Vue.nextTick(() => {
                        // $("#refAuthor").tagEditor({
                        //     forceLowercase: false
                        // })
                        $('#refAuthor').tagEditor('destroy');
                        $('#refAuthor').tagEditor({
                            initialTags: this.articleToBack.authors,
                            forceLowercase: false,
                        });

                    })
                }else{
                    $('#refAuthor').tagEditor('destroy');
                    $('#refAuthor').tagEditor({
                        initialTags: this.articleToBack.authors,
                        forceLowercase: false,
                    });
                }

            })
            this.showUploadArticleDialog = false;
            // this.articleToBack.status = status;
        },

        addArticleClick(){
            this.editArticleDialog = true;
            this.addorEdit='Add';
            $("#refTitle").val('');

            if ($("#refAuthor").nextAll().length == 0)//如果不存在tageditor,则创建一个
                Vue.nextTick(() => {
                    $("#refAuthor").tagEditor({
                        forceLowercase: false
                    })
                })

            $('#refAuthor').tagEditor('destroy');
            $('#refAuthor').tagEditor({
                initialTags:  [''],
                forceLowercase: false,
            });
            $("#refJournal").val('');
            $("#volumeIssue").val('');
            $("#refPages").val('');
            $("#refDate").val('');
            $("#refLink").val('');

            this.doi ='';
        },

        submitReference(){
            let references = new Array();

            var ref_lines = $("#dynamic-table tr");
            for (i = 1; i < ref_lines.length; i++) {
                var ref_prop = ref_lines.eq(i).children("td");
                if (ref_prop != 0) {
                    var ref = {};
                    ref.title = ref_prop.eq(0).text();
                    if (ref.title == "No data available in table")
                        break;
                    ref.authors = ref_prop.eq(1).text().split(",");
                    ref.date = ref_prop.eq(2).text();
                    ref.journal = ref_prop.eq(3).text();
                    ref.volume = ref_prop.eq(4).text();
                    ref.pageRange = ref_prop.eq(5).text();
                    ref.link = ref_prop.eq(6).text();
                    ref.doi = ref_prop.eq(7).text();
                    references.push(ref);
                }
            }

            let data = {
                id:this.modelId,
                references:JSON.stringify(references)
            }

            $.ajax({
                type:'PUT',
                url:'/modelItem/references',
                data: data,
                // dataType: "json",
                // accept: 'application/json',
                success:(res)=>{
                    if (res.code == -1) {
                        this.confirmLogin()
                    }else{
                        let data = res.data
                        if(data=='suc'){
                            this.$alert("Change references successfully!", 'Success', {
                                type: 'success',
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.reload();
                                }
                            })
                        }else if(data=='version'){
                            this.$alert("Your edit has been submit, please wait for the contributor to handle it.", 'Success', {
                                type: 'success',
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.reload();
                                }
                            })
                        }

                    }


                }
            })

        },

        isCurrentItem(row){
            let urls = window.location.href.split('/');
            let oid = urls[urls.length-1].substring(0,36);
            return(oid==row.oid);
        },
        claim(){
            $.get("/user/load",{},(result)=>{
                let json = result;
                if (json.code !== 0) {
                    this.confirmLogin();
                }
                else {
                    this.authorshipFormVisible = true;
                }
            })
        },
        feedBack(){
            $.get("/user/load",{},(result)=>{
                let json = result;
                if (json.code !== 0) {
                    this.confirmLogin();
                }
                else {
                    window.location.href = "/user/userSpace#/feedback"
                }
            })
        },
        useMyInfo(){
            $.get("/user/getUserSimpleInfo",{},(result)=>{
                if(result.code==-1){
                    this.confirmLogin();
                }else{
                    let data = result.data;
                    this.authorshipForm.name = data.name;
                    this.authorshipForm.ins = data.org;
                    this.authorshipForm.email = data.email;
                    this.authorshipForm.homepage = data.homepage;
                }
            })
        },
        submitAuthorship(){
            let urls = window.location.href.split('/');
            let oid = urls[urls.length-1].substring(0,36);
            this.authorshipForm.oid = oid;
            axios.post("/modelItem/claimAuthorship",this.authorshipForm)
                .then((result) => {
                    result = result.data;
                    if(result.code==-1){
                        this.confirmLogin();
                    }else if(result.code==-2){
                        this.$alert(result.msg, 'Tip', {
                            type:'info',
                            confirmButtonText: 'OK',
                            callback: action => {

                            }
                        });
                    }else{
                        if(result.data.method=='update'){
                            this.$alert("Added successfully!", 'Success', {
                                type: 'success',
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.reload();
                                }
                            });
                        }else {
                            this.$alert("Submitted successfully, please wait for review.", 'Tip', {
                                type: 'success',
                                confirmButtonText: 'OK',
                                callback: action => {
                                    this.authorshipFormVisible = false
                                }
                            });
                        }
                    }
                })
        },


        confirmLogin(){
            window.sessionStorage.setItem("history", window.location.href);
            const language = window.localStorage.getItem("language");

            if (language !== "zh-cn"){
                var loginTip = "This function requires an account, please login first."
                var login = "Log in"
                var tip = "Tip"
            }else {
                var loginTip = "该操作需要一个账户，请先登录"
                var login = "登录"
                var tip = "提示"
            }

            this.$confirm('<div style=\'font-size: 18px\'>' + loginTip + '</div>', tip, {
                dangerouslyUseHTMLString: true,
                confirmButtonText: login,
                cancelButtonClass: 'fontsize-15',
                confirmButtonClass: 'fontsize-15',
                type: 'info',
                center: true,
                showClose: false,
            }).then(() => {
                window.location.href = "/user/login";
            }).catch(() => {

            });
        },

        edit() {
            $.ajax({
                type: "GET",
                url: "/user/load",
                data: {},
                cache: false,
                async: false,
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success: (data) => {
                    if (data.code !== 0) {
                        this.confirmLogin()

                    }
                    else {
                        let href = window.location.href;
                        let hrefs = href.split('/');
                        let oid = hrefs[hrefs.length - 1].split("#")[0];
                        window.open("/user/userSpace#/model/manageModelItem/"+oid);
                        // window.location.href = "/user/userSpace#/model/manageModelItem/"+oid;
                        // this.editDialogOpen()


                        // $.ajax({
                        //     type: "GET",
                        //     url: "/modelItem/getUserOidByOid",
                        //     data: {
                        //         oid: oid
                        //     },
                        //     cache: false,
                        //     async: false,
                        //     xhrFields: {
                        //         withCredentials: true
                        //     },
                        //     crossDomain: true,
                        //     success: (json) => {
                        //         // if(json.data==data.oid){
                        //         window.sessionStorage.setItem("editModelItem_id", oid)
                        //         window.location.href = "/user/createModelItem";
                        //         // }
                        //         // else{
                        //         //     alert("You are not the model item's author, please contact to the author to modify the model item.")
                        //         // }
                        //     }
                        // });
                    }
                }
            })
        },

        editDialogOpen(){
            // let href = window.location.href;
            // let hrefs = href.split('/');
            // let oid = hrefs[hrefs.length - 1].split("#")[0];
            this.modelId = itemInfo.id;
            this.editModelItemDialog = true
        },

        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
        },
        link(event) {
            window.open(event);
            // let refLink = $(".refLink");
            // for (i = 0; i < refLink.length; i++) {
            //     if (event.currentTarget == refLink[i]) {
            //         window.open(this.refTableData[i].links);
            //     }
            // }
            //console.log(event.currentTarget);
        },
        jump() {
            $.ajax({
                type: "GET",
                url: "/user/load",
                data: {},
                cache: false,
                async: false,
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success: (data) => {
                    if (data.code !== 0) {
                        this.confirmLogin()
                    }
                    else {
                        let arr = window.location.href.split("/");
                        let bindOid = arr[arr.length - 1].split("#")[0];
                        this.setSession("bindOid", bindOid);
                        switch (this.relateType) {
                            case "modelItem":
                                window.open("/user/userSpace#/model/createModelItem", "_blank")
                                break;
                            case "conceptualModel":
                                window.open("/user/userSpace#/model/createConceptualModel", "_blank")
                                break;
                            case "logicalModel":
                                window.open("/user/userSpace#/model/createLogicalModel", "_blank")
                                break;
                            case "computableModel":
                                window.open("/user/userSpace#/model/createComputableModel", "_blank")
                                break;
                            case "concept":
                                window.open("/user/userSpace#/community/createConcept", "_blank")
                                break;
                            case "spatialReference":
                                window.open("/user/userSpace#/community/createSpatialReference", "_blank")
                                break;
                            case "template":
                                window.open("/user/userSpace#/community/createTemplate", "_blank")
                                break;
                            case "unit":
                                window.open("/user/userSpace#/community/createUnit", "_blank")
                                break;
                        }
                        this.dialogTableVisible = false;
                    }
                }
            })
        },

        checkRelatedData(item) {
            let curentId = document.location.href.split("/");
            return curentId[0] + "//" + curentId[2] + "/dataItem/" + item.id;
        },
        //add related models

        addRelatedModel() {

            if (this.user.email == '') {
                this.confirmLogin()

            } else {
                this.searchAddModelPage = 1
                this.searchAddRelatedModels = []
                this.addModelsSearchText = ""
                this.selectedModels = []
                this.selectedModelsOid = []

                this.nomore = ''
                this.addRelatedModelsDialogVisible = true

            }

        },

        searchRelatedModels() {

            this.nomoreflag = false
            if (this.value1 === '1') {

                this.addSearchFromUser()
            } else if (this.value1 === '2') {

                this.addSearchFromAll()
            }
        },
        clearSearchResult() {

            this.searchAddRelatedModels = []
            this.nomore = ''

        },
        loadAddMore(e) {

            let that = this
            if (e.target.scrollHeight - e.target.clientHeight - e.target.scrollTop < 10 && this.nomore === '') { //到达底部100px时,加载新内容

                clearTimeout(this.timer);

                this.timer = setTimeout(() => {
                        that.searchAddModelPage += 1// 这里加载数据..


                        if (this.value1 === '1') {
                            that.addSearchFromUser()
                        } else if (this.value1 === '2') {
                            that.addSearchFromAll()
                        }


                    },
                    500)

            }


        },
        addSearchFromUser() {

            // let data = {
            //     searchText: this.addModelsSearchText,
            //     page: this.searchAddModelPage,
            //     asc: false,
            //     pageSize: 5,
            //     email: this.user.email
            // }

            let reqData = {
                searchText: this.addModelsSearchText,
                page: this.searchAddModelPage,
                pagesize: 5,
                asc: false,
                authorEmail: this.user.email
            }

            let that = this
            this.loading = true
            if (this.nomore === '') {
                axios.post("/dataItem/queryListOfAuthorSelf/", reqData)
                    .then((res) => {

                        if (res.status === 200) {
                            if (res.data.data.list.length === 0) {
                                that.nomore = "nomore"
                                that.loading = false
                            } else {
                                that.loading = false
                                that.searchAddRelatedModels = that.searchAddRelatedModels.concat(res.data.data.list)
                            }

                        }


                    })
            }


        },
        addSearchFromAll() {


            let arr = []
            arr.push(this.addModelsSearchText)
            let data = {
                page: this.searchAddModelPage,
                asc: 1,
                pageSize: 5,
                searchContent: arr

            }


            let that = this
            this.loading = true
            //searchFromAll
            axios.post("/dataItem/searchFromAll", data)
                .then((res) => {

                    if (res.status === 200) {

                        that.loading = false
                        that.searchAddRelatedModels = that.searchAddRelatedModels.concat(res.data.data.content)
                    }


                })

        },
        selectRelatedModel(item, e) {

            if (this.selectedModels.indexOf(item.name) > -1) {
                e.currentTarget.className = "is-hover-shadow models_margin_style"

                this.getRidOf(item.name, this.selectedModels)
                this.getRidOf(item.id, this.selectedModelsOid)
            } else {
                e.currentTarget.className = "is-hover-shadow models_margin_style selectedModels"

                this.selectedModels.push(item.name)
                this.selectedModelsOid.push(item.id)
            }


        },
        getRidOf(e, arr) {
            arr.splice(arr.indexOf(e), 1)
        },
        relatedToCurrentData() {

            if (this.selectedModelsOid.length === 0) {
                alert("pleasa select model first!")
            } else {

                let curentId = document.location.href.split("/");

                let dataItemFindDTO = {
                    dataId: curentId[curentId.length - 1],
                    relatedModels: this.selectedModelsOid
                }

                axios.post("/dataItem/data", dataItemFindDTO)


                    .then((res) => {
                        if (res.status === 200) {
                            alert("Cgts,related models successfully!")

                        }

                    })


            }

        },


        showRelatedModels() {
            this.dataNums = 5
            this.searchAddRelatedModels = []
            this.searchRelatedModelsDialogVisible = true
            relatedModelsSearchText = ""
            this.RelatedModels(this.dataNums)


        },
        searchFromRelatedModels() {
            //todo search from show related models
        },
        //函数节流防抖
        loadMore(e) {

            if (!this.nomoreflag) {
                if (e.target.scrollHeight - e.target.clientHeight - e.target.scrollTop < 10) { //到达底部100px时,加载新内容

                    clearTimeout(this.timer);

                    this.timer = setTimeout(() => {
                            this.dataNums += 5// 这里加载数据..
                            this.RelatedModels(this.dataNums)
                        },
                        500)

                }
            }

        },

        RelatedModels(more) {
            let curentId = document.location.href.split("/");
            let that = this
            this.loading = true
            this.nomore = false
            axios.get("/dataItem/allrelateddata", {
                params: {
                    id: curentId[curentId.length - 1],
                    more: more
                }
            })
                .then((res) => {
                    if (res.status == 200) {
                        that.loading = false
                        //todo 传回来数组为空时
                        if (res.data.data[0].all === "all") {
                            that.nomore = "no more"
                            that.nomore = true
                            that.loading = false

                        } else {
                            that.allRelatedModels = that.allRelatedModels.concat(res.data.data)
                            that.loading = false
                        }

                    }

                })
        },

        searchInit(scope){
            this.pageOption_all.currentPage = 1;
            this.pageOption_my.currentPage = 1;
            this.search(scope);
        },

        //relate search
        search(scope) {
            let data;
            if(scope=="all"){
                // this.pageOption_all.currentPage = 1;
                data = {
                    asc: this.pageOption_all.sortAsc,
                    page: this.pageOption_all.currentPage,
                    pageSize: this.pageOption_all.pageSize,
                    searchText: this.pageOption_all.relateSearch.trim(),
                    sortField: this.pageOption_all.sortField,
                }
            }else {
                // this.pageOption_my.currentPage = 1;
                data = {
                    asc: this.pageOption_my.sortAsc,
                    page: this.pageOption_my.currentPage,
                    pageSize: this.pageOption_my.pageSize,
                    searchText: this.pageOption_my.relateSearch,
                    sortField: this.pageOption_my.sortField,
                };
            }
            let url, contentType;

            if (this.relateType == "exLink" || this.relateType == "dataSpaceFile"){
                return;
            }


            if(scope=="all") {
                url = "/" + this.relateType + "/list";
            }else{
                url = "/" + this.relateType + "/listByAuthor";
            }

            axios.post(url, data).then(res => {
                let json = res.data;
                if (json.code == 0) {
                    let data = json.data;
                    console.log(data)

                    if(scope=="all") {
                        this.pageOption_all.total = data.total;
                        this.pageOption_all.pages = data.pages;
                        this.pageOption_all.searchResult = data.list;
                        this.pageOption_all.users = data.users;
                        this.pageOption_all.progressBar = false;
                        this.pageOption_all.paginationShow = true;
                    }else{
                        this.pageOption_my.total = data.total;
                        this.pageOption_my.pages = data.pages;
                        this.pageOption_my.searchResult = data.list;
                        this.pageOption_my.users = data.users;
                        this.pageOption_my.progressBar = false;
                        this.pageOption_my.paginationShow = true;
                    }

                }
                else {
                    console.log("query error!")
                }

            });
        },

        receiveModuleMsg(msg){
            let m_code = msg.m_code
            let m_msg = msg.m_msg

            if(m_code==-1){
                if(m_msg.toLowerCase()==='nologin'&&this.dialogTableVisible){
                    this.confirmLogin()
                    this.dialogTableVisible = false
                }
            }else if(m_code==0){
                if(m_msg.toLowerCase()==='pending'&&this.dialogTableVisible){
                    this.dialogTableVisible = false
                    window.location.reload();
                }
            }else if(m_code==1){
                if(m_msg.toLowerCase()==='suc'&&this.dialogTableVisible){
                    this.dialogTableVisible = false
                    window.location.reload();
                }
            }
        },

        getRelatedResources() {
            //从地址栏拿到oid
            let arr = window.location.href.split("/");
            let id = arr[arr.length - 1].split("#")[0];
            let data = {
                id: id,
            };
            $.ajax({
                type: "GET",
                url: "/modelItem/relatedResources/" + id,
                // data: data,
                async: true,
                success: (json) => {
                    if (json.code == 0) {
                        let data = json.data;
                        console.log(data)

                        this.tableData = data;

                    }else  if (json.code == -1) {
                        this.confirmLogin()
                    }
                    else {
                        console.log("query error!")
                    }
                }
            })
        },

        handlePageChange(val) {
            if(this.activeName_dialog=="my") {
                this.pageOption_my.currentPage = val;
            }else{
                this.pageOption_all.currentPage = val;
            }
            this.search(this.activeName_dialog);
        },

        hasAdded(row){
            for(let i=0;i<this.tableData.length;i++){
                let data = this.tableData[i];
                let oid1,oid2;
                if(data.oid!=undefined){
                    oid1 = data.oid;
                    oid2 = row.oid;
                }else{
                    oid1 = data.id;
                    oid2 = row.id;
                }
                if(oid1==oid2){
                    return true;
                }
            }
        },

        handleDelete(index, row) {
            this.tableData.splice(index, 1);
        },

        handleEdit(index, row) {
            console.log(row);
            row.type=this.relateType
            let flag = false;
            for (i = 0; i < this.tableData.length; i++) {
                let tableRow = this.tableData[i];
                if (tableRow.id == row.id) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                if(this.relateType=="modelItem"){
                    this.$set(row,"relation","Connected with");
                }
                this.tableData.push(row);
            }
        },



        addRelateResources(){
            let formData=new FormData();

            let stringInfo = []
            this.tableData.forEach(function (item, index) {
                let relateItem={}
                relateItem.id = item.id
                relateItem.type = item.type
                if(item.type=='localFile'){
                    formData.append("resources",item.raw);
                }
                if(item.type=='exLink'){
                    relateItem.content = item.content
                    relateItem.name = item.name
                }
                if(item.type=='dataSpaceFile'){
                    // relateItem.oid = item.oid
                    relateItem.url = item.url
                    relateItem.name = item.name
                }
                stringInfo.push(relateItem);
            })

            // let file = new File([JSON.stringify(stringInfo)],'ant.txt',{
            //     type: 'text/plain',
            // });
            formData.append("stringInfo", JSON.stringify(stringInfo))


            let arr = window.location.href.split("/");
            let id = arr[arr.length - 1].split("#")[0];

            let url = '';

            $.ajax({
                type: "POST",
                url: "/modelItem/addRelateResources/"+id,
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                async: true,
                success: (result) => {
                    let info = result.data
                    if(info === 'suc'){
                        this.$alert('Success!', 'Tip', {
                            type:'success',
                            confirmButtonText: 'OK',
                            callback: action => {
                                this.dialogTableVisible = false;
                                window.location.reload();
                            }
                        });
                    }else if(info === 'version'){
                        this.$alert("Your edit has been submit, please wait for the contributor to handle it.", 'Success', {
                            type: 'success',
                            confirmButtonText: 'OK',
                            callback: action => {
                                window.location.reload();
                            }
                        })
                    }


                },
                error: (json) => {
                    this.$alert('Submitted failed!', 'Error', {
                        type:'error',
                        confirmButtonText: 'OK',
                        callback: action => {

                        }
                    });
                }
            })
        },

        checkPersonData(i, j) {
            try {
                this.runTemplatesValueIndex[0] = i
                this.inOutParam[0] = i
                this.runTemplatesValueIndex[1] = j
            }
            catch (e) {
                console.log(e)
            }
            finally {
                this.showDataChose = true;
                this.$nextTick(()=>{
                    this.$refs.userDataSpace.getFilePackage();
                })
            }
        },

        selectDataspaceFile(file){
            // if (this.editMetadata == true){
            //     this.targetFile.push(file.address)
            //     return
            // }
            if (this.targetFile.indexOf(file) > -1) {
                // for (var i = 0; i < this.targetFile.length; i++) {
                //     if (this.targetFile[i] === file) {
                //         //删除
                //         this.targetFile.splice(i, 1);
                //         // this.downloadDataSetName.splice(i, 1)
                //         break
                //     }
                // }
            } else {
                this.targetFile.push(file);
            }
        },

        // selectDataspaceFile(file) {
        //     this.targetFile = file
        // },

        removeDataspaceFile(file) {
            if (file.address.substr(0,4)!="http"){
                file.address = "https://geomodeling.njnu.edu.cn/dataTransferServer" + file.address
            }

            targetFileLength = this.targetFile.length
            for (var i =0; i<targetFileLength; i++){
                if (this.targetFile[i].address == file.address){
                    this.targetFile.splice(i, 1);
                    break
                }
            }

            if (this.targetFile.indexOf(file) > -1) {
                for (var i = 0; i < this.targetFile.length; i++) {
                    if (this.targetFile[i] === file) {
                        //删除
                        this.targetFile.splice(i, 1);
                        // this.downloadDataSetName.splice(i, 1)
                        break
                    }
                }
            }
        },

        selectDataFromPersonal(){
            if (this.editMetadata == true && this.metaDataTab == 'fifth'){
                if (this.targetFile.length > 1){
                    this.$message({
                        message: '仅支持上传单个文件！',
                        type: 'warning'
                    });
                }else {
                    this.ModelMetaData.mdt.runTemplates[this.runTemplatesValueIndex[0]].configInfo[this.runTemplatesValueIndex[1]].value = this.targetFile[0].address
                    // this.targetFile = []
                    this.showDataChose = false
                }
                return;
            }
            if (this.editMetadata == true && this.metaDataTab == 'fourth'){
                if (this.targetFile.length > 1){
                    this.$message({
                        message: '仅支持上传单个文件！',
                        type: 'warning'
                    });
                }else {
                    this.ModelMetaData.di.inOutPara[this.inOutParam[0]].defaultValue = this.targetFile[0].address
                    // this.targetFile = []
                    this.showDataChose = false
                }
                return;
            }
            for (let i = 0; i < this.targetFile.length; i++) {
                let file = {
                    name:this.targetFile[i].name + "." + this.targetFile[i].suffix,
                    url:this.targetFile[i].address,
                    type:"dataSpaceFile"
                }
                for(let tableEle of this.tableData){
                    if(tableEle.url == file.url){
                        this.$alert('You have selected ' + file.name, 'Tip', {
                                type:"warning",
                                confirmButtonText: 'OK',
                                callback: ()=>{
                                    return
                                }
                            }
                        );
                        return
                    }
                }

                this.tableData.push(file)
            }

            this.showDataChose = false
        },

        handleClose(done) {
            this.$confirm(this.htmlJson.AreYouSureToClose)
                .then(_ => {
                    done();
                })
                .catch(_ => {
                });
        },

        addRelation(order) {
            let newUrl = window.location.href.split("#")[0]+"#"+order;
            var stateObject = {};
            var title = document.title;
            history.pushState(stateObject,title,newUrl);
            $.ajax({
                type: "GET",
                url: "/user/load",
                data: {},
                cache: false,
                async: false,
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success: (data) => {
                    if (data.code !== 0) {
                        this.confirmLogin()
                    }
                    else {
                        switch (order) {
                            case 1:
                                this.relateType = "modelItem";
                                this.typeName = "Model Item";
                                this.activeName_dialog = "my";
                                break;
                            case 2:
                                this.relateType = "conceptualModel";
                                this.typeName = "Conceptual Model";
                                this.activeName_dialog = "my";
                                break;
                            case 3:
                                this.relateType = "logicalModel";
                                this.typeName = "Logical Model";
                                this.activeName_dialog = "my";
                                break;
                            case 4:
                                this.relateType = "computableModel";
                                this.typeName = "Computable Model";
                                this.activeName_dialog = "my";
                                break;
                            case 5:
                                this.relateType = "concept";
                                this.typeName = "Concept & Semantic";
                                this.activeName_dialog = "all";
                                break;
                            case 6:
                                this.relateType = "spatialReference";
                                this.typeName = "Spatiotemporal Reference";
                                this.activeName_dialog = "all";
                                break;
                            case 7:
                                this.relateType = "template";
                                this.typeName = "Data Template";
                                this.activeName_dialog = "all";
                                break;
                            case 8:
                                this.relateType = "unit";
                                this.typeName = "Unit & Metric";
                                this.activeName_dialog = "all";
                                break;
                            case 9:
                                this.relateType = "dataItem";
                                this.typeName = "Data Item";
                                this.activeName_dialog = "my";
                                break;
                            case 10:
                                this.relateType = "dataHub";
                                this.typeName = "Data Hub";
                                this.activeName_dialog = "my";
                                break;
                            case 11:
                                this.relateType = "dataMethod";
                                this.typeName = "Data Method";
                                this.activeName_dialog = "my";
                                break;
                        }
                        var linkTip = ""
                        const lang = window.localStorage.getItem("language")
                        if (lang === "en-us"){
                            linkTip = "Link to "
                        }else {
                            linkTip = "关联至"
                        }
                        this.relateTitle = linkTip+this.itemInfo.name;
                        this.tableData = [];

                        this.pageOption_my.currentPage = 1;
                        this.pageOption_my.searchResult = [];
                        this.pageOption_my.relateSearch = "";

                        this.pageOption_all.currentPage = 1;
                        this.pageOption_all.searchResult = [];
                        this.pageOption_all.relateSearch = "";

                        let arr = window.location.href.split("/");
                        let id = arr[arr.length - 1].split("#")[0];
                        let targetType = arr[arr.length - 2]

                        this.dialogTableVisible = true;
                        this.$nextTick(()=>{
                            this.$refs.linkRelatedItemModule.manualInit(id,targetType,this.relateType)
                        })

                        // this.getRelation();
                        // this.search(this.activeName_dialog);
                        // if(this.activeName_dialog!="all"){
                        //     this.search("all");
                        // }

                    }
                }
            })

        },

        addRelatedResouece(){
            this.relateType = 'concept'
            $.ajax({
                type: "GET",
                url: "/user/load",
                data: {},
                cache: false,
                async: false,
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success: (data) => {
                    if (data.code !== 0) {
                        this.confirmLogin()
                    }
                    else {
                        this.activeName_dialog = 'all'

                        this.tableData = [];

                        this.pageOption_my.currentPage = 1;
                        this.pageOption_my.searchResult = [];
                        this.pageOption_my.relateSearch = "";

                        this.pageOption_all.currentPage = 1;
                        this.pageOption_all.searchResult = [];
                        this.pageOption_all.relateSearch = "";

                        this.getRelatedResources();
                        this.search(this.activeName_dialog);
                        if(this.activeName_dialog!="all"){
                            this.search("all");
                        }
                        this.relatedResourceVisible = true;
                    }
                }
            })

        },

        uploadRemove(file, fileList) {
            this.relateFile = [];
        },

        uploadChange(file, fileList) {
            this.relateFile.splice(0,1);
            this.relateFile.push(file);
            let fileMeta = {
                name:file.name,
                oid:file.uid,
                raw:file.raw,
                type:'localFile'
            }
            this.tableData.push(fileMeta)
        },

        getTypeImg(row){
            switch (row.type){
                case "concept":
                    return '../../static/img/model/semantics.png'
                    break;
                case "spatialReference":
                    return '../../static/img/model/spatialreference.png'
                case "template":
                    return '../../static/img/model/template.png'
                case "unit":
                    return '../../static/img/model/unit.png'
            }

        },

        getTypeExpress(row){
            switch (row.type){
                case "concept":
                    return 'Concept & Semantic'
                case "spatialReference":
                    return 'Spatiotemporal Reference'
                case "template":
                    return 'Data Template'
                case "unit":
                    return 'Unit & Metric'
                case "dataSpaceFile":
                    return 'File'
            }
        },

        checkContent(row){
            if(row.type=='file'){
                return
            }else if(row.type=='link'){
                window.open(row.content)
            }else {
                window.open('/repository/'+row.type+'/'+row.id)
            }
        },

        addExLink(){
            if(this.exLink.name.trim()==''){
                this.$alert('Please input the name of the link', 'Tip', {
                        type:"warning",
                        confirmButtonText: 'OK',
                        callback: ()=>{
                            return
                        }
                    }
                );
                return
            }
            if(this.exLink.content.trim()==''){
                this.$alert('Please input the content of the link', 'Tip', {
                        type:"warning",
                        confirmButtonText: 'OK',
                        callback: ()=>{
                            return
                        }
                    }
                );
                return
            }

            if(!this.checkUrlValid(this.exLink.content)){
                this.$alert('Please input a valid link!', 'Tip', {
                        type:"warning",
                        confirmButtonText: 'OK',
                        callback: ()=>{
                            return
                        }
                    }
                );
                return
            }

            let oid = this.generateGUID()
            let linkMeta = {
                name : this.exLink.name,
                oid : oid,
                content : this.exLink.content,
                type:'exLink'
            }

            this.exLupdateAliasink = {}
            this.tableData.push(linkMeta)
        },

        checkUrlValid(url){
            this.checkUrlValidLoading = true
            let strRegex =  "((https|http|ftp|rtsp|mms)?://)"
                + "(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)";
            let re=new RegExp(strRegex);
            let flag = re.test(url)
            this.checkUrlValidLoading = false
            return flag
        },
        // handleSuccess(result,file,fileList){
        //     let fileMeta = {
        //
        //     }
        //     this.tableData.push(fileMeta)
        //     // this.upload_data_dataManager(uploadSource);
        // },

        collapse(){
            console.log('aa')
            $('#authorship0').collapse()
        },

        setRelatedModelItemsPage(){
            this.relatedModelItemsPage = [];
            let relatedModelItems = this.relation.modelItems;
            if(relatedModelItems!=null) {
                for (i = 0; i < relatedModelItems.length; i++) {
                    if (i === this.relationPageSize) break;
                    this.relatedModelItemsPage.push(relatedModelItems[i]);
                }
            }
        },

        getContributors(){

            axios.get('/modelItem/contributors',{
                params:{
                    id:this.itemInfo.id
                }
            }).then(
                res=>{
                    this.contributors = res.data.data

                    this.$nextTick(()=>{

                    })
                }
            )

        },

        riseUser(contributor,index){
            let tmp = this.lightenContributor
            this.lightenContributor = contributor
            this.$set(this.contributors,index,tmp)
        },

        checkObjAllProptNull(obj){
            let pros = Object.values(obj)
            return pros.every( ele => {
                console.log('111:' + ele)
                return ele === null || ele.length==0 || ele===""
            })
        },

        changeRelateType(activeType){
            this.relateType = activeType
            this.searchInit(this.activeName_dialog)
        },

        submitMetaData(metaData){
            try {this.ModelMetaData.di.formula =  tinyMCE.get('singleFormula').getContent()}catch (err){console.log(err)}
            try {this.ModelMetaData.mdt.instruction =  tinyMCE.get('singleDescription').getContent()}catch (err){console.log(err)}
            try {this.ModelMetaData.mdt.modelAnalysis.modelCalibration =  tinyMCE.get('singleModelCalibration').getContent()}catch (err){console.log(err)}
            try {this.ModelMetaData.mdt.modelAnalysis.modelValidation =  tinyMCE.get('singleModelValidation').getContent()}catch (err){console.log(err)}
            try {this.ModelMetaData.mdt.modelAnalysis.effectivenessAnalysis =  tinyMCE.get('singleEffectivenessAnalysis').getContent()}catch (err){console.log(err)}
            try {this.ModelMetaData.mdt.modelAnalysis.uncertaintyAnalysis =  tinyMCE.get('singleUncertaintyAnalysis').getContent()}catch (err){console.log(err)}
            try {this.ModelMetaData.mdt.modelAnalysis.sensitivityAnalysis =  tinyMCE.get('singleSensitivityAnalysis').getContent()}catch (err){console.log(err)}
            if (this.judgeRequired() == false){
                return
            }
            axios.post("/modelItem/saveMetaData/" + this.modelId, metaData).then(res => {
                console.log(metaData)
                if (res.data.code == -1){
                    this.$message({
                        message: "模型元数据上传失败！请稍后重试！",
                        type: "warning"
                    });
                }else {
                    this.$message({
                        message: "模型元数据上传成功！",
                        type: "success"
                    });
                    location.reload();
                }
            })
            this.editMetadata = false
        },

        addRunTemplates(){
            this.ModelMetaData.mdt.runTemplates.push(
                {
                    configInfo:[
                        {
                            name: '',
                            type: '',
                            value: ''
                        }
                    ],
                    desc: ''
                }
            )
        },

        removeRunTemplates(i){
            this.ModelMetaData.mdt.runTemplates.splice(i, 1)
        },

        addConfig(i){
            this.ModelMetaData.mdt.runTemplates[i].configInfo.push(
                {
                    name: '',
                    type: '',
                    value: ''
                }
            )
        },

        removeConfig(i){
            this.ModelMetaData.mdt.runTemplates[i].configInfo.pop()
        },

        removeAnalysis(i){
            if (i==0){
                this.ModelMetaData.mdt.modelAnalysis.modelCalibration.pop()
            }
            else if (i == 1){
                this.ModelMetaData.mdt.modelAnalysis.modelValidation.pop()
            }
            else if (i==2){
                this.ModelMetaData.mdt.modelAnalysis.effectivenessAnalysis.pop()
            }
            else if (i==3){
                this.ModelMetaData.mdt.modelAnalysis.uncertaintyAnalysis.pop()
            }
            else if (i==4){
                this.ModelMetaData.mdt.modelAnalysis.sensitivityAnalysis.pop()
            }
        },

        addAnalysis(i){
            if (i==0){
                this.ModelMetaData.mdt.modelAnalysis.modelCalibration.push('')
            }
            else if (i == 1){
                this.ModelMetaData.mdt.modelAnalysis.modelValidation.push('')
            }
            else if (i==2){
                this.ModelMetaData.mdt.modelAnalysis.effectivenessAnalysis.push('')
            }
            else if (i==3){
                this.ModelMetaData.mdt.modelAnalysis.uncertaintyAnalysis.push('')
            }
            else if (i==4){
                this.ModelMetaData.mdt.modelAnalysis.sensitivityAnalysis.push('')
            }
            console.log(this.ModelMetaData.mdt.modelAnalysis.modelCalibration)
        },

        addTerm(){
            this.ModelMetaData.di.term.push({
                name: '',
                desc: ''
            })
        },

        removeTerm(i){
            this.ModelMetaData.di.term.splice(i,1)
        },

        addInOut(){
            this.ModelMetaData.di.inOutPara.push({
                name: '',
                type: '',
                desc: '',
                unit: '',
                limit: '',
                defaultValue: '',
                format: ''
            })
        },

        removeInOut(i){
            this.ModelMetaData.di.inOutPara.splice(i,1)
        },

        addSubModel(){
            this.ModelMetaData.mo.subModel.push(
                {
                    modelName: '',
                    version: '',
                    versionId: '',
                    usage: ''
                }
            )
        },

        removeSubModel(i){
            this.ModelMetaData.mo.subModel.splice(i,1)
        },

        handleRemove(file, fileList) {
            console.log(file, fileList);
        },

        handlePictureCardPreview(file) {
            this.dialogImageUrl = file.url;
            this.dialogVisible = true;
        },

        picExceed(){
            this.$message({
                message: '单个模型结构条目仅支持上传一张图例！'
            });
        },

        modeUpload(option){
            this.fileList.push(option)
            let uploadUrl = 'http://221.226.60.2:8082/data'
            // 使用form表单的数据格式
            const formData  = new FormData()
            // 将上传文件数组依次添加到参数paramsData中
            this.fileList.forEach((it, index) => {
                formData.append('datafile', it.file)
            });
            this.fileList = []
            // 将表单数据添加到参数paramsData中
            formData.append('name', 'model Structure')
            fetch(uploadUrl, {method: 'POST', body: formData})
                .then(response => {
                    if(response.status == 200){
                        return response.json()
                    }
                    else throw new Error('服务端错误')
                })
                .then((data) => {
                    let downloadUrl = uploadUrl + '/' + data.data.id
                    this.ModelMetaData.mo.modelStructure.legend = downloadUrl
                })
                .catch(alert)
        },

        addStructure(){
            this.ModelMetaData.mo.modelStructure.push(
                {
                    legend: '',
                    type: ''
                }
            )
        },

        removeStructure(i){
            // console.log(i)
            this.ModelMetaData.mo.modelStructure.splice(i,1)
        },

        addPurpose(){
            this.ModelMetaData.pie.modelPurpose.push('')
        },

        removePurpose(i){
            this.ModelMetaData.pie.modelPurpose.splice(i,1)
        },

        addAssumnption(){
            this.ModelMetaData.pie.modelAssumption.push('')
        },

        removeAssumption(i){
            this.ModelMetaData.pie.modelAssumption.splice(i,1)
        },

        addPrinciple(){
            this.ModelMetaData.pie.basicPrinciple.push('')
        },

        removePrinciple(i){
            this.ModelMetaData.pie.basicPrinciple.splice(i,1)
        },

        addclassificationInfo(){
            this.ModelMetaData.pie.classificationInfo.push('')
        },

        removeclassificationInfo(i){
            this.ModelMetaData.pie.classificationInfo.splice(i,1)
        },

        addCodeLanguage(){
            this.ModelMetaData.pie.codeLanguage.push('')
        },

        removeCodeLanguage(i){
            this.ModelMetaData.pie.codeLanguage.splice(i,1)
        },

        handleClose2(tag) {
            this.ModelMetaData.mp.modelName.modelAlias.splice(this.ModelMetaData.mp.modelName.modelAlias.indexOf(tag), 1);
        },

        showInput() {
            this.inputVisible = true;
            this.$nextTick(_ => {
                this.$refs.saveTagInput.$refs.input.focus();
            });
        },

        showInput4() {
            this.inputVisible4 = true;
            this.$nextTick(_ => {
                this.$refs.saveTagInput4.$refs.input.focus();
            });
        },

        showInput5() {
            this.inputVisible5 = true;
            this.$nextTick(_ => {
                this.$refs.saveTagInput5.$refs.input.focus();
            });
        },

        showInput6() {
            this.inputVisible6 = true;
            this.$nextTick(_ => {
                this.$refs.saveTagInput6.$refs.input.focus();
            });
        },

        handleInputConfirm() {
            let inputValue = this.inputValue;
            if (inputValue) {
                console.log(this.ModelMetaData.mp.modelName)
                this.ModelMetaData.mp.modelName.modelAlias.push(inputValue);
            }
            this.inputVisible = false;
            this.inputValue = '';
        },

        handleClose3(tag) {
            this.ModelMetaData.mp.keywords.splice(this.ModelMetaData.mp.keywords.indexOf(tag), 1);
        },

        handleClose4(tag, i) {
            this.ModelMetaData.mp.reference[i].author.splice(this.ModelMetaData.mp.reference[i].author.indexOf(tag), 1);
        },

        handleClose5(tag, i) {
            this.ModelMetaData.mp.reference[i].agency.splice(this.ModelMetaData.mp.reference[i].agency.indexOf(tag), 1);
        },

        handleClose6(tag, i) {
            this.ModelMetaData.pie.codeLanguage.splice(this.ModelMetaData.pie.codeLanguage.indexOf(tag), 1);
        },

        showInput3() {
            this.inputVisible2 = true;
            this.$nextTick(_ => {
                this.$refs.saveTagInput2.$refs.input.focus();
            });
        },

        handleInputConfirm3() {
            let inputValue = this.inputValue2;
            if (inputValue) {
                this.ModelMetaData.mp.keywords.push(inputValue);
            }
            this.inputVisible2 = false;
            this.inputValue2 = '';
        },

        handleInputConfirm4(i) {
            let inputValue = this.inputValue4;
            if (inputValue) {
                this.ModelMetaData.mp.reference[i].author.push(inputValue);
            }
            this.inputVisible4 = false;
            this.inputValue4 = '';
        },

        handleInputConfirm5(i) {
            let inputValue = this.inputValue5;
            if (inputValue) {
                this.ModelMetaData.mp.reference[i].agency.push(inputValue);
            }
            this.inputVisible5 = false;
            this.inputValue5 = '';
        },

        handleInputConfirm6(i) {
            let inputValue = this.inputValue6;
            if (inputValue) {
                this.ModelMetaData.pie.codeLanguage.push(inputValue);
            }
            this.inputVisible6 = false;
            this.inputValue6 = '';
        },

        addTitle(i, item, item2){
            this.ModelMetaData.mp[item][item2][i].title.push('')
        },

        removeTitle(i, j, item, item2){
            this.ModelMetaData.mp[item][item2][j].title.splice(i,1)
        },

        addAffliation(i, item, item2){

            this.ModelMetaData.mp[item][item2][i].affiliation.push('')
        },

        removeAffliation(i, j, item, item2){
            this.ModelMetaData.mp[item][item2][j].affiliation.splice(i,1)
        },

        addEmail(i, item, item2){
            this.ModelMetaData.mp[item][item2][i].email.push('')
        },

        removeEmail(i, j, item, item2){
            this.ModelMetaData.mp[item][item2][j].email.splice(i,1)
        },

        addPhone(i, item, item2){
            this.ModelMetaData.mp[item][item2][i].phone.push('')
        },

        removePhone(i, j, item, item2){
            this.ModelMetaData.mp[item][item2][j].phone.splice(i,1)
        },

        addFax(i, item, item2){
            this.ModelMetaData.mp[item][item2][i].fax.push('')
        },

        removeFax(i, j, item, item2){
            this.ModelMetaData.mp[item][item2][j].fax.splice(i,1)
        },

        addPersonalPage(i, item, item2){
            this.ModelMetaData.mp[item][item2][i].personalPage.push('')
        },

        removePersonalPage(i, j, item, item2){
            this.ModelMetaData.mp[item][item2][j].personalPage.splice(i,1)
        },

        addAuthor(item, item2){
            this.ModelMetaData.mp[item][item2].push({
                contactorName: {
                    fullName: '',
                    surname: ''
                },
                contactorAddress: {
                    country: '',
                    province: '',
                    city: '',
                    address: '',
                    postCode: ''
                },
                title: [''],
                affiliation: [''],
                email: [''],
                phone: [''],
                fax: [''],
                personalPage: ['']
            })
        },

        removeAuthor(i, item, item2){
            this.ModelMetaData.mp[item][item2].splice(i,1)
        },

        addSpatialReference(){
            this.ModelMetaData.mp.referenceSystem.spatialReference.push({
                type: '',
                name: ''
            })
        },

        removeSpatialReference(i){
            this.ModelMetaData.mp.referenceSystem.spatialReference.splice(i, 1)
        },

        addRelateItem(){
            this.ModelMetaData.mp.relateItem.push({
                projectName: '',
                projectId: '',
                fundingAgency: '',
                type: ''
            })
        },

        removeRelateItem(i){
            this.ModelMetaData.mp.relateItem.splice(i, 1)
        },

        addReference(){
            this.ModelMetaData.mp.reference.push({
                title: '',
                author: [],
                agency: [],
                press: '',
                reel: '',
                issue: '',
                page: '',
                publishDate:'',
                url: ''
            })
        },

        removeReference(i){
            this.ModelMetaData.mp.reference.splice(i, 1)
        },

        beforeUpload(file) {
            const FILE_NAME = file.name
            if (FILE_NAME.substring(FILE_NAME.lastIndexOf('.')) !== '.jpg' && FILE_NAME.substring(FILE_NAME.lastIndexOf('.')) !== '.png') {
                this.$message.warning('仅支持.jpg和.png文件')
                return false
            }
        },

        tabclick(tab, event){
            if (tab.index == 3){
                tinymce.init({
                    selector: '#singleFormula',
                    plugins: "kityformula-editor",
                    toolbar: "kityformula-editor",
                    readonly: this.isCreator == 'false'
                })
            }
            if (tab.index == 4){
                tinymce.init({
                    selector: '#singleDescription',
                    plugins: 'link',
                    toolbar: 'link',
                    readonly: this.isCreator == 'false',
                    file_picker_callback: function (callback, value, meta) {
                        //文件分类
                        var filetype='.pdf, .txt, .doc, .docx';
                        //后端接收上传文件的地址
                        var upurl='http://221.226.60.2:8082/data';
                        //模拟出一个input用于添加本地文件
                        var input = document.createElement('input');
                        input.setAttribute('type', 'file');
                        input.setAttribute('accept', filetype);
                        input.click();
                        input.onchange = function() {
                            var file = this.files[0];
                            console.log(file.name);
                            const formData = new FormData()
                            formData.append('datafile', file);
                            formData.append('name', 'Instructions');
                            fetch(upurl, {method: 'POST', body: formData})
                                .then(response => {
                                    if(response.status == 200){
                                        return response.json()
                                    }
                                    else throw new Error('服务端错误')
                                })
                                .then((data) => {
                                    alert('上传成功')
                                    let downloadUrl = upurl + '/' + data.data.id
                                    callback(downloadUrl);
                                })
                                .catch(alert)
                        };
                    },
                })
            }
        },

        collapseChange(newVal){
            if (newVal['1'] == 1 || newVal['2'] == 1 || newVal['3'] == 1 || newVal['4'] == 1 || newVal['5'] == 1){
                tinymce.init({
                    selector: '#singleModelCalibration',
                    readonly: this.isCreator == 'false'
                    // plugins: "code kityformula-editor",
                    // toolbar: "code kityformula-editor",
                })
            }
            if (newVal['1'] == 2 || newVal['2'] == 2 || newVal['3'] == 2 || newVal['4'] == 2 || newVal['5'] == 2){
                tinymce.init({
                    selector: '#singleModelValidation',
                    readonly: this.isCreator == 'false'
                    // plugins: "code kityformula-editor",
                    // toolbar: "code kityformula-editor",
                })
            }
            if (newVal['1'] == 3 || newVal['2'] == 3 || newVal['3'] == 3 || newVal['4'] == 3 || newVal['5'] == 3){
                tinymce.init({
                    selector: '#singleEffectivenessAnalysis',
                    readonly: this.isCreator == 'false'
                    // plugins: "code kityformula-editor",
                    // toolbar: "code kityformula-editor",
                })
            }
            if (newVal['1'] == 4 || newVal['2'] == 4 || newVal['3'] == 4 || newVal['4'] == 4 || newVal['5'] == 4){
                tinymce.init({
                    selector: '#singleUncertaintyAnalysis',
                    readonly: this.isCreator == 'false'
                    // plugins: "code kityformula-editor",
                    // toolbar: "code kityformula-editor",
                })
            }
            if (newVal['1'] == 5 || newVal['2'] == 5 || newVal['3'] == 5 || newVal['4'] == 5 || newVal['5'] == 5){
                tinymce.init({
                    selector: '#singleSensitivityAnalysis',
                    readonly: this.isCreator == 'false'
                    // plugins: "code kityformula-editor",
                    // toolbar: "code kityformula-editor",
                })
            }
        },

        querySearch(queryString, cb) {
            var classification = this.classificationList;
            var results = queryString ? classification.filter(this.createFilter(queryString)) : classification;
            // 调用 callback 返回建议列表的数据
            cb(results);
        },

        createFilter(queryString) {
            return (classification) => {
                return (classification.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
            };
        },

        judgeIsCreator(){
            axios.get('/modelItem/matchLoginerCreater/'+this.modelId
            ).then(
                res => {
                    let data = res.data.data
                    this.isCreator = data
                }
            )
        },

        objectValueAllEmpty(data) {
            let flag = true;
            let _this = this;
            function judgeChildren(obj) {
                for(var key in obj){
                    if(typeof obj[key] === 'string'){
                        // 字符串
                        if((obj[key] != null) && (obj[key] != '') && (obj[key] != undefined)){
                            flag = false
                            //可以在此将key赋值给一个全局变量，判断是哪个属性值为空
                            return
                        }
                    }else if(Array.isArray(obj[key])){
                        // 数组
                        obj[key].forEach((item)=>{
                            judgeChildren(item)
                        })
                    }else{
                        //对象
                        judgeChildren(obj[key])
                    }
                }
            }
            judgeChildren(data);
            return flag;
        },

        objectValueHasEmpty(data) {
            let flag = false;
            let _this = this;
            function judgeChildren(obj) {
                for(var key in obj){
                    if(typeof obj[key] === 'string'){
                        // 字符串
                        if((obj[key] == null) || (obj[key] == '') || (obj[key] == undefined)){
                            flag = true
                            //可以在此将key赋值给一个全局变量，判断是哪个属性值为空
                            return
                        }
                    }else if(Array.isArray(obj[key])){
                        // 数组
                        obj[key].forEach((item)=>{
                            judgeChildren(item)
                        })
                    }else{
                        //对象
                        judgeChildren(obj[key])
                    }
                }
            }
            judgeChildren(data);
            return flag;
        },

        judgeFieldHasEmpty(obj, fields){
            flag = false
            objLength = obj.length
            fieldLength = fields.length
            for (var i=0; i<objLength; i++){
                for (var j=0; j<fieldLength; j++){
                    if ((obj[i][fields[j]] == null) || (obj[i][fields[j]] == '') || (obj[i][fields[j]] == undefined)){
                        flag = true
                    }
                }
            }
            return flag
        },

        judgeRequired(){
            if (this.ModelMetaData.mp.modelVersion.numbering == '' || this.ModelMetaData.mp.modelVersion.purpose == undefined || this.ModelMetaData.mp.modelVersion.date == '' || this.ModelMetaData.mp.modelVersion.author[0].contactorName.fullName == '' || this.ModelMetaData.mp.modelVersion.author[0].contactorName.surname == ''
            || this.ModelMetaData.mp.descriptionInfo.descAbstract == '' || this.ModelMetaData.mp.developInfo.startDate == '' || this.ModelMetaData.mp.developInfo.process == '' || this.ModelMetaData.mp.developInfo.developer[0].contactorName.fullName == '' || this.ModelMetaData.mp.developInfo.developer[0].contactorName.surname == ''
            || this.ModelMetaData.mp.metaDataVersion.purpose == '' || this.ModelMetaData.mp.metaDataVersion.date == '' || this.ModelMetaData.mp.metaDataVersion.author[0].contactorName.fullName == '' || this.ModelMetaData.mp.metaDataVersion.author[0].contactorName.surname == ''){
                this.$message({
                    message: '请完善基本信息！',
                    type: 'warning'
                });
                return false
            }else if (this.ModelMetaData.pie.modelCategory == undefined || this.objectValueHasEmpty(this.ModelMetaData.pie.classificationInfo) == true){
                this.$message({
                    message: '请完善设计理念！',
                    type: 'warning'
                });
                return false
            }else if ((this.objectValueAllEmpty(this.ModelMetaData.mp.referenceSystem) == false) && (this.objectValueHasEmpty(this.ModelMetaData.mp.referenceSystem) == true)){
                this.$message({
                    message: '请完善基本信息！',
                    type: 'warning'
                });
                return false
            }else if (this.ModelMetaData.mp.modelVersion.purpose != 1 && this.ModelMetaData.mp.modelVersion.changeContent == ''){
                this.$message({
                    message: '请完善基本信息！',
                    type: 'warning'
                });
                return false
            }else if (this.ModelMetaData.mp.metaDataVersion.purpose != 1 && this.ModelMetaData.mp.metaDataVersion.changeContent == ''){
                this.$message({
                    message: '请完善基本信息！',
                    type: 'warning'
                });
                return false
            }else if (this.ModelMetaData.mp.publishInfo.publishMode.openAccess == true && this.ModelMetaData.mp.publishInfo.publishMode.openSourceProtocol == ''){
                this.$message({
                    message: '请完善基本信息！',
                    type: 'warning'
                });
                return false
            }else if ((this.objectValueAllEmpty(this.ModelMetaData.mp.publishInfo) == false) && (this.ModelMetaData.mp.publishInfo.publishMode.openAccess === '' || this.ModelMetaData.mp.publishInfo.publishMode.accessMethod == '' || this.ModelMetaData.mp.publishInfo.publishMode.usageMethod == '' || this.ModelMetaData.mp.publishInfo.publishMethod.transmitMode == ''
                || this.ModelMetaData.mp.publishInfo.publishMethod.accessLocation == '' || this.ModelMetaData.mp.publishInfo.publishDate == undefined || this.ModelMetaData.mp.publishInfo.publisher[0].contactorName.fullName == '' || this.ModelMetaData.mp.publishInfo.publisher[0].contactorName.surname == '')){
                this.$message({
                    message: '请完善基本信息！',
                    type: 'warning'
                });
                return false
            }else if ((this.objectValueAllEmpty(this.ModelMetaData.pie.applicationScope) == false) && (this.objectValueHasEmpty(this.ModelMetaData.pie.applicationScope) == true)){
                this.$message({
                    message: '请完善设计理念！',
                    type: 'warning'
                });
                return false
            }else if (this.ModelMetaData.mo.modelStructure.legend != '' & (this.ModelMetaData.mo.modelStructure.type == '' || this.ModelMetaData.mo.modelStructure.type == undefined)){
                this.$message({
                    message: '请完善模型架构！',
                    type: 'warning'
                });
                return false
            }else if ((this.objectValueAllEmpty(this.ModelMetaData.di.inOutPara) == false) && (this.judgeFieldHasEmpty(this.ModelMetaData.di.inOutPara, ['name', 'type', 'defaultValue', 'format'])==true)){
                this.$message({
                    message: '请完善模型数据！',
                    type: 'warning'
                });
                return false
            }else if ((this.objectValueAllEmpty(this.ModelMetaData.mdt.runTemplates) == false) && (this.objectValueHasEmpty(this.ModelMetaData.mdt.runTemplates) == true)){
                this.$message({
                    message: '请完善运行测试！',
                    type: 'warning'
                });
                return false
            }else if ((this.objectValueAllEmpty(this.ModelMetaData.mp.relateItem) == false) && (this.judgeFieldHasEmpty(this.ModelMetaData.mp.relateItem, ['projectName', 'projectId', 'fundingAgency']))){
                this.$message({
                    message: '请完善基本信息！',
                    type: 'warning'
                });
                return false
            }else if ((this.objectValueAllEmpty(this.ModelMetaData.mp.reference) == false) && (this.judgeFieldHasEmpty(this.ModelMetaData.mp.reference, ['title', 'author', 'press']))){
                this.$message({
                    message: '请完善基本信息！',
                    type: 'warning'
                });
                return false
            }else if ((this.objectValueAllEmpty(this.ModelMetaData.di.term) == false) && (this.objectValueHasEmpty(this.ModelMetaData.di.term) == true)){
                this.$message({
                    message: '请完善模型数据！',
                    type: 'warning'
                });
                return false
            }
            return true
        },

        relationSortChange(sort){
            console.log(sort);
            let order = sort.order==="ascending";
            let field = sort.column.label.toLowerCase();
            if(this.activeName_dialog==="my"){
                this.pageOption_my.sortAsc=order;
                this.pageOption_my.sortField=field;
            }else{
                this.pageOption_all.sortAsc=order;
                this.pageOption_all.sortField=field;
            }
            this.search(this.activeName_dialog);
        },
    },

    created(){
        this.itemInfo = itemInfo;
        this.relation = relation;
        this.metadata = itemInfo.metadata;
        this.modelReferences = modelReferences;

        this.getContributors()
    },

    mounted() {
        this.htmlJson = this.getStorage("userSpaceAll")
        let vthis = this


        this.setRelatedModelItemsPage();

        this.lightenContributor = author
        this.$refs.mainContributorAvatar.insertAvatar(this.lightenContributor.avatar)
        this.$refs.mainContributorAvatar1.insertAvatar(this.lightenContributor.avatar)

        this.model_classifications = classifications;

        // let href = window.location.href;
        // let hrefs = href.split('/');
        // let oid = hrefs[hrefs.length - 1].split("#")[0];
        this.modelId = itemInfo.id;

        let currenturl = window.location.href;
        let dataitemid = currenturl.split("/");

        let that = this
        // axios.get("/modelItem/briefRelatedData", {
        //     params: {
        //         id: this.itemInfo.id,
        //     }
        // })
        //     .then((res) => {
        //         that.related3Models = res.data.data
        //
        //         if (that.related3Models.length === 0) {
        //             that.relatedModelIsNull = true;
        //             that.relatedModelNotNull = false
        //         } else {
        //             that.relatedModelNotNull = true
        //             that.relatedModelIsNull = false;
        //         }
        //     })

        axios.get("/user/load")
            .then((res) => {
                console.log(res);
                if (res.status == 200) {
                    if (res.data.code === 0) {
                        this.user.email = res.data.data.email;
                        this.user.accessId = res.data.data.accessId;
                        this.user.name = res.data.data.name;
                        this.user.avatar = res.data.data.avatar;
                    }

                }
            })

        // this.getComments();


        $(document).on('mouseover mouseout','.block_head',function(e){
            let editBtn=$(e.currentTarget).children(".blockEdit");
            if(editBtn.css("display")=="none"){
                editBtn.css("display","block");
            }else{
                editBtn.css("display","none");
            }
        });

        $(document).on('mouseover mouseout','.flexRowSpaceBetween',function(e){

            let deleteBtn=$(e.currentTarget).children().eq(1).children(".delete");
            if(deleteBtn.css("display")=="none"){
                deleteBtn.css("display","block");
            }else{
                deleteBtn.css("display","none");
            }

        });

        let descHeight = $("#description .block_content").height();
        if (descHeight > 300) {
            $("#description .block_content").css("overflow", "hidden")
            $("#description .block_content").css("height", "250px")

            $(".fullPaper").removeClass("hide");
        }

        let refs = this.modelReferences;
        // refs = refs.replaceAll(/\\/g,"").replaceAll(/\"\{/g,"{").replaceAll(/\}\"/g,"}")
        if (refs != null) {
            let jsonArray = [];
            for (i = 0; i < refs.length; i++) {
                let json = JSON.parse(refs[i].replaceAll(/\\/g,"").replaceAll(/\"\{/g,"{").replaceAll(/\}\"/g,"}"));
                json.authors = json.authors.join(", ");
                jsonArray.push(json);
            }
            // console.log(json);
            this.refTableData = jsonArray;
        }

        $("#fullPaper").click(function () {
            $("#description .block_content").css("overflow", "inherit");
            $("#description .block_content").css("height", "auto");
            $(".fullPaper").remove();
        })

        let qrcodes = document.getElementsByClassName("qrcode");
        for(i=0;i<qrcodes.length;i++) {
            new QRCode(document.getElementsByClassName("qrcode")[i], {
                text: window.location.href,
                width: 200,
                height: 200,
                colorDark: "#000000",
                colorLight: "#ffffff",
                correctLevel: QRCode.CorrectLevel.H
            });
        }

        let panes = $(".el-tab-pane")

        let relate = itemInfo.relate;

        if(relate.computableModels.length>0){
            this.activeName = "Computable Model";
        }else if(relate.conceptualModels.length>0){
            this.activeName = "Conceptual Model";
        }else if(relate.logicalModels.length>0){
            this.activeName = "logical Model";
        }

        if(relate.modelRelationList.length>0){
            this.activeName1 = "Model Item";
        }else if(relate.dataItems.length>0){
            this.activeName1 = "Data Item";
        }

        if(itemInfo.references.length>0){
            this.activeName2 = "Reference";
        }else if(relate.concepts.length>0||relate.spatialReferences.length>0||relate.templates.length>0||
        relate.units.length>0||relate.dataSpaceFiles.length>0||relate.exLinks.length>0||relate.localFiles.length>0){
            this.activeName2 = "Resource";
        }

        let splits = window.location.href.split("#");
        if(splits.length>1){
            let number = Number(splits[1]);
            switch (number){
                case 1:
                    this.activeName1 = "Model Item";
                    break;
                case 9:
                    this.activeName1 = "Data Item";
                    break;
                case 4:
                    this.activeName = "Computable Model";
                    break;
                case 2:
                    this.activeName = "Conceptual Model";
                    break;
                case 3:
                    this.activeName = "Logical Model";
                    break;
            }
        }

        $(document).on("click", ".refClose", function () {
            vthis.dynamicTable.row($(this).parents("tr")).remove().draw();
            //$(this).parents("tr").eq(0).remove();
            console.log($("tbody tr"));
            if ($("tbody tr").eq(0)[0].innerText == "No data available in table") {
                $("#dynamic-table").css("display", "none")
            }
        });

        // document.body.scrollTop = 0;
        // document.documentElement.scrollTop = 0;

        axios.get('/modelItem/getMetaData/'+this.modelId
        ).then(
            res => {
                if(res.data.code==-1){
                    this.confirmLogin()
                }else{
                    let data = res.data.data
                    // 如果是null，则不操作
                    if (data == null){
                        return
                    }
                    this.ModelMetaData = {...data}
                    this.ModelMetaDataList.push(this.ModelMetaData)
                    // this.insertMetaData(this.ModelMetaData, data)
                }
            }
        )

        this.judgeIsCreator()

        this.nowLanguage = window.localStorage.getItem("language");


    }
})

$(function () {

    $(".ab").click(function () {

            if (!$(this).hasClass('transform180'))
                $(this).addClass('transform180')
            else
                $(this).removeClass('transform180')
        }
    );

})

function initTinymce(idStr){
    tinymce.remove(idStr)
    tinymce.init({
        selector: idStr,
        height: 350,
        plugins: [
            "advcode advlist autolink codesample image imagetools ",
            " lists link media noneditable powerpaste preview",
            " searchreplace table visualblocks wordcount"
        ],
        toolbar:
            "undo redo | fontselect | fontsizeselect | bold italic underline | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image",
        file_picker_types: 'image',
        file_picker_callback: function (cb, value, meta) {
            var input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', 'image/*');
            input.onchange = function () {
                var file = input.files[0];

                var reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = function () {
                    var img = reader.result.toString();
                    cb(img, {title: file.name});
                }
            };
            input.click();
        },
        images_dataimg_filter: function (img) {
            return img.hasAttribute('internal-blob');
        }
    });
}