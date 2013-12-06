$(document).ready(function() {

//LoadXML('XMLHolder','/assets/server.xml');
    if ($('#tree').length > 0)
    {
        var filepath = '../data/entries_hotels.xml'

        $(function() {
            new XMLTree({fpath: filepath, container: '#tree', startExpanded: true,
            });

            //alert ($('ul[class="xmltree startExpanded"]').length)
            $('ul[class="xmltree startExpanded"]').prop('id', 'xmltree');
        });

        jQuery.expr[':'].Contains = function(a, i, m) {
            return (a.textContent || a.innerText || "").toUpperCase().indexOf(m[3].toUpperCase()) >= 0;
        };

        //scroll to first child when filtering
        var container = $('body');

        $('#search_input').focus().keyup(function(e) {
            var filter = $(this).val();
            if (filter) {
                $('#xmltree').find("li:not(:Contains(" + filter + "))").parent().hide();
                $('#xmltree').find("li:Contains(" + filter + ")").parent().show();
                $("#xmltree").find("li:Contains(" + filter + ")").children().show();
                $('#xmltree').highlight(filter);

                //scroll to first child when filtering
                var container = $('body');
                var scrollTo = $('#xmltree').children().find("li:Contains(" + filter + ")").first();

                console.log(scrollTo);
                container.scrollTop(
                        scrollTo.offset().top - container.offset().top + container.scrollTop()
                        );
                //scroll to first child when filtering- ends
            }
            else {
                container.offset().top = 0;
                $('#xmltree').find("li").children().slideDown();
                $('#xmltree').unhighlight();
            }
        });

        $('#search_input').focus().keydown(function(e) {
            $('#xmltree').unhighlight();
        });
    }
});