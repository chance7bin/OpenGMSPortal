const sectionData_zh = [{"label": "1集成任务配置流程", "children": []}, {
    "label": "2为模型和数据方法加载数据",
    "children": []
}, {
    "label": "3运行集成任务",
    "children": []
}]

const supportDoc_zh = [{
    "title": "1集成任务配置流程",
    "content": "<p>在本示例中，我们将向您展示如何在OpenGMS中在线运行集成任务。</p>\n" +
    "<p>首先，您需要登录，然后您可以<a href='http://geomodeling.njnu.edu.cn/computableModel/integratedModel'>点击这里</a>，或者点击模型应用和用户空间中的快速卡片，打开集成建模页面。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image001.jpg\" ></p>\n" +
    "<p>单击“开始”模块以开始配置任务。</p>\n" +
    "<p>单击“选择模型”按钮以选择要添加到集成任务中的计算模型。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image002.jpg\" ></p>\n" +
    "<p>单击“选中”按钮将模型添加到任务中。在本示例中，首先添加模型“TAUDEM – Pit remove”。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image003.jpg\" ></p>\n" +
    "<p>该模型的输入和输出将显示在绘制区域下方的数据总线中。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image004.jpg\" ></p>\n" +
    "<p>您也可以单击“选择数据方法”按钮添加数据方法。在本示例中，我们选择了“Tiffclip”方法来处理模型使用的数据。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image005.jpg\" ></p>\n" +
    "<p>单击绘制区域中的单元格，所有输入和输出单元格将显示在右侧栏中。将它们拖到绘制区域中。我们希望模型“Tandem – Pit Remove”使用数据方法“Tiffclip”的输出，因此需要连接它们的数据单元。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image006.jpg\" ></p>\n" +
    "<p>然后我们将更多模型添加到集成任务中。并连接它们的输出和输入。</p>\n" +
    "<p>需要用到的模型如下所示:</p>\n" +
    "<p>‘TAUDEM- D-Infinity Flow Direction’,’TAUDEM - D8 Contributing Area’,’ TauDEM_D8FlowDir’, ‘TAUDEM - D-Infinity Contributing Area’,’TAUDEM - Slope Area Combination’,’TAUDEM - D8 Extreme Upslope Value’,’ Stream Definition By Threshold’,’ TauDEM - StreamNet’. </p>\n" +
    "<p>数据连接如下图所示：</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image007.png\" ></p>\n"
}, {
    "title": "2为模型和数据方法加载数据",
    "content": "<p>将页面滑动到数据总线，单击“配置”按钮：</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image008.png\" ></p>\n" +
    "<p>或单击图表中的数据单元格，单击右侧边栏中的“配置”按钮。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image009.jpg\" ></p>\n" +
    "<p>将显示数据配置对话框：</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image010.jpg\" ></p>\n" +
    "<p>单击按钮<span><img src=\"/static/img/demo/demo2/image011.jpg\" ></span>加载数据。或者您可以单击面板“当前任务”中的按钮，配置一个任务中的所有数据。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image012.jpg\" ></p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image013.jpg\" ></p>\n" +
    "<p>本示例中使用到的数据显示在下表中。</p>\n" +
    "<table align=\"center\" class=MsoTableGrid border=1 cellspacing=0 cellpadding=0\n" +
    " style='border-collapse:collapse;border:none'>\n" +
    " <tr style='height:1.5pt'>\n" +
    "  <td width=297 valign=top style='width:222.8pt;border:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt;height:1.5pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>任务模块</span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border:solid windowtext 1.0pt;\n" +
    "  border-left:none;padding:0cm 5.4pt 0cm 5.4pt;height:1.5pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>输入数据</span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border:solid windowtext 1.0pt;\n" +
    "  border-left:none;padding:0cm 5.4pt 0cm 5.4pt;height:1.5pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>下载地址</span></p>\n" +
    "  <p class=MsoNormal><span lang=EN-US style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr style='height:1.5pt'>\n" +
    "  <td width=297 rowspan=6 valign=top style='width:222.8pt;border:solid windowtext 1.0pt;\n" +
    "  border-top:none;padding:0cm 5.4pt 0cm 5.4pt;height:1.5pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>Tiffclip</span></p>\n" +
    "  </td>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt;height:1.5pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>AOICilp.shp</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt;height:1.5pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/95c5cccf-8461-4281-a53d-cc8fcc270894'>http://221.226.60.2:8082/data/95c5cccf-8461-4281-a53d-cc8fcc270894</a></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>AOICilp.dbf</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/b328ce74-2197-43b7-930f-238043308e30'>http://221.226.60.2:8082/data/b328ce74-2197-43b7-930f-238043308e30</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>AOICilp.sbn</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/113956c7-9d56-4261-81b1-66b6640a7714'>http://221.226.60.2:8082/data/113956c7-9d56-4261-81b1-66b6640a7714</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>AOICilp.sbx</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/7e0d5ab0-5820-4eb1-992f-7755cea0ca6e'>http://221.226.60.2:8082/data/7e0d5ab0-5820-4eb1-992f-7755cea0ca6e</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>AOICilp.shx</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/6aaec736-d700-417d-91fe-250154bf41b2'>http://221.226.60.2:8082/data/6aaec736-d700-417d-91fe-250154bf41b2</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>Afghanistan.tif</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/f927c2d2-9b81-4120-8bb3-24b348f80c10'>http://221.226.60.2:8082/data/f927c2d2-9b81-4120-8bb3-24b348f80c10</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=297 rowspan=2 valign=top style='width:222.8pt;border:solid windowtext 1.0pt;\n" +
    "  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>TAUDEM - D8 Contributing Area</span></p>\n" +
    "  </td>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>Outlets</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/385b4459-8b3b-43df-b921-d262d3704e70'>http://221.226.60.2:8082/data/385b4459-8b3b-43df-b921-d262d3704e70</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>WeightGrid</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/ca0fc392-4666-487f-919b-17fd9dc74a71'>http://221.226.60.2:8082/data/ca0fc392-4666-487f-919b-17fd9dc74a71</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=297 valign=top style='width:222.8pt;border:solid windowtext 1.0pt;\n" +
    "  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>TAUDEM - D-Infinity Contributing Area</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>Outlets</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/d8542bf4-c809-48b2-a856-44f72883f9bf'>http://221.226.60.2:8082/data/d8542bf4-c809-48b2-a856-44f72883f9bf</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=297 valign=top style='width:222.8pt;border:solid windowtext 1.0pt;\n" +
    "  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>TAUDEM - Slope Area Combination</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>Input Suffixes</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/3f7e1c60-0463-42c7-b0e7-613b35186d88'>http://221.226.60.2:8082/data/3f7e1c60-0463-42c7-b0e7-613b35186d88</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=297 rowspan=2 valign=top style='width:222.8pt;border:solid windowtext 1.0pt;\n" +
    "  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <table class=MsoNormalTable border=0 cellspacing=0 cellpadding=0 width=283\n" +
    "   style='width:212.0pt;border-collapse:collapse'>\n" +
    "   <tr style='height:15.6pt'>\n" +
    "    <td width=283 nowrap rowspan=2 style='width:212.0pt;padding:0cm 5.4pt 0cm 5.4pt;\n" +
    "    height:15.6pt'>\n" +
    "    <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "    style='font-size:11.0pt;color:black'>TAUDEM - D8 Extreme Upslope Value</span></p>\n" +
    "    </td>\n" +
    "    <td style='height:15.6pt;border:none' width=0 height=21></td>\n" +
    "   </tr>\n" +
    "   <tr style='height:15.6pt'>\n" +
    "    <span lang=EN-US style='font-size:11.0pt;font-family:����;color:black'>\n" +
    "    <td style='height:15.6pt;border:none' width=0 height=21></td>\n" +
    "    </span>\n" +
    "   </tr>\n" +
    "  </table>\n" +
    "  <p class=MsoNormal></p>\n" +
    "  </td>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal><span lang=EN-US style='font-size:11.0pt;color:black'>D8FlowDirection</span></span></p>\n" +
    "  <p class=MsoNormal><span lang=EN-US style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal><span lang=EN-US style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/22c9c545-d17c-4a9a-84ca-68bc9465db26'>http://221.226.60.2:8082/data/22c9c545-d17c-4a9a-84ca-68bc9465db26</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>Outlets</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/278e7ae4-3e55-4616-a42f-e4c4b03fda50'>http://221.226.60.2:8082/data/278e7ae4-3e55-4616-a42f-e4c4b03fda50</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=297 valign=top style='width:222.8pt;border:solid windowtext 1.0pt;\n" +
    "  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>Stream Definition By Threshold</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>ThresholdValue</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/a4d556e6-f30c-4d4b-ac80-254dcd1f372c'>http://221.226.60.2:8082/data/a4d556e6-f30c-4d4b-ac80-254dcd1f372c</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    " <tr>\n" +
    "  <td width=297 valign=top style='width:222.8pt;border:solid windowtext 1.0pt;\n" +
    "  border-top:none;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>TauDEM - StreamNet</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=118 valign=top style='width:88.55pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>PitRemove</span></span></p>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'>&nbsp;</span></p>\n" +
    "  </td>\n" +
    "  <td width=277 valign=top style='width:207.4pt;border-top:none;border-left:\n" +
    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
    "  <p class=MsoNormal align=center style='text-align:center'><span lang=EN-US\n" +
    "  style='font-size:11.0pt;color:black'><a href='http://221.226.60.2:8082/data/a0633329-55f0-4a87-8399-0e65503fad65'>http://221.226.60.2:8082/data/a0633329-55f0-4a87-8399-0e65503fad65</a></span></span></p>\n" +
    "  </td>\n" +
    " </tr>\n" +
    "</table>\n"
}, {
    "title": "3运行集成任务",
    "content": "<p>在加载完所有必要数据后，单击“结束”以标识此任务的结束。然后，我们可以单击“执行”按钮来配置该任务的基本信息并运行它。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image013.jpg\" ></p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image014.png\" ></p>\n" +
    "<p>已完成的任务模块将变为绿色，正在运行的任务模块将标记为黄色。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image015.jpg\" ></p>\n" +
    "<p>当所有任务模块都变为绿色时，集成任务运行成功。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image016.png\" ></p>\n" +
    "<p>点击数据总线上的按钮可以下载一个输出数据。每个任务模块一旦运行完成，就可以下载它的输出数据。</p>\n" +
    "<p align=\"center\"><img src=\"/static/img/demo/demo2/image017.png\" ></p>\n" +
    "<p>您可以在您的个人空间中查看您运行的所有集成任务。</p>\n" +
    "<p align=\"center\" width='502'><img src=\"/static/img/demo/demo2/image018.png\" ></p>\n"
}]