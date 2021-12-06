Vue.component("editDescriptionModule",
    {
        template: '#editDescriptionModule',
        props: {
            a: {
                type: String,
                default: 'default'
            }
        },
        data() {
            return {
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

                languageList:[],
            }
        },
        computed: {},
        methods: {

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

        },
        created() {
            this.languageList = window.languageList
        },
        mounted() {}
    }
)