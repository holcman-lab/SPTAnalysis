classdef PluginAdapter
    methods (Static)
        function time_wins = loadTimeWindows(fname)
            a = xmlread(fname);
            xRoot = a.getDocumentElement;
            
            time_wins = struct();
            
            c = xRoot.getFirstChild;
            while ~isempty(c)
                if strcmp(c.getNodeName, 'timeWins')
                    d = c.getFirstChild;
                    while ~isempty(d)
                        if strcmp(d.getNodeName, 'dur')
                            time_wins.w_dur = str2num(string(d.getFirstChild.getNodeValue));
                        elseif strcmp(d.getNodeName, 'ovlp')
                            time_wins.w_ovlp = str2num(string(d.getFirstChild.getNodeValue));
                        elseif strcmp(d.getNodeName, 'tMin')
                            time_wins.w_tmin = str2num(string(d.getFirstChild.getNodeValue));
                        elseif strcmp(d.getNodeName, 'tMax')
                            time_wins.w_tmax = str2num(string(d.getFirstChild.getNodeValue));
                        end
                        
                        d = d.getNextSibling;
                    end
                end
                c = c.getNextSibling;
            end
        end
        
        function [tab_w, tab_w_tids] = loadTrajectories(fname)
            a = xmlread(fname);
            xRoot = a.getDocumentElement;
            
            tab_w = {};
            tab_w_tids = {};
            
            c = xRoot.getFirstChild;
            while ~isempty(c)
                if strcmp(c.getNodeName, 'base_trajs')
                    d = c.getFirstChild;
                    twin_cpt = 1;
                    while ~isempty(d)
                        if strcmp(d.getNodeName, 'wins')
                            dd = d.getFirstChild;

                            tab = [];
                            tab_tids = [];
                            while ~isempty(dd)
                                if strcmp(dd.getNodeName, 'trajs')
                                    e = dd.getAttributes;
                                    for uu=0:(e.getLength-1)
                                        tmp = e.item(uu);
                                        tmp2 = strsplit(string(tmp), "=");
                                        if strcmp(tmp2{1}, 'id')
                                            t_id = str2num(tmp2{2}(2:(end-1)));
                                        elseif strcmp(tmp2{1}, 'from')
                                            tab_tids = [tab_tids str2num(tmp2{2}(2:(end-1)))];
                                        end
                                    end

                                    e = dd.getFirstChild;
                                    while ~isempty(e)
                                        if strcmp(e.getNodeName, 'points')
                                            tmp = strsplit(string(e.getFirstChild.getNodeValue), '\n');
                                            i = 1;
                                            while i < length(tmp)
                                                tmp2 = sscanf(tmp{i}, '%g %g %g %g');
                                                tab = [tab; t_id tmp2(1:3)'];
                                                i = i + 1;
                                            end
                                        end
                                        e = e.getNextSibling;
                                    end
                                end
                                dd = dd.getNextSibling;
                            end

                            tab_w{twin_cpt} = tab;
                            tab_w_tids{twin_cpt} = tab_tids;
                            
                            twin_cpt = twin_cpt + 1;
                        end
                        d = d.getNextSibling;
                    end
                end
                c = c.getNextSibling;
            end
        end

        function well_algos = loadWells(fname)
            a = xmlread(fname);

            xRoot = a.getDocumentElement;

            well_algos = {};

            c = xRoot.getFirstChild;
            while ~isempty(c)
                if strcmp(c.getNodeName, 'wells')
                    d = c.getFirstChild;
                    while ~isempty(d)
                        res = string(d.getNodeValue);
                        tmp = PluginAdapter.xmlreadstring(res{1});

                        e = tmp.getFirstChild.getFirstChild;
                        while ~isempty(e)
                            if strcmp(e.getNodeName, 'entry')
                                algo = struct();
                                f = e.getFirstChild;
                                while ~isempty(f)
                                    if strcmp(f.getNodeName, 'key')
                                        algo.name = string(f.getFirstChild.getNodeValue);
                                        algo.name = algo.name{1};
                                    elseif strcmp(f.getNodeName, 'value')
                                        algo.detects = {};
                                        g = f.getFirstChild;
                                        while ~isempty(g)
                                            if strcmp(g.getNodeName, 'entry')
                                                detect = struct();
                                                h = g.getFirstChild;
                                                while ~isempty(h)
                                                    if strcmp(h.getNodeName, 'key')
                                                        i = h.getFirstChild;
                                                        while ~isempty(i)
                                                            if ~strcmp(i.getNodeName, '#text')
                                                                detect.params = struct();
                                                                detect.params.type = string(i.getNodeName);
                                                                detect.params.type = detect.params.type{1};

                                                                j = i.getFirstChild;
                                                                while ~isempty(j)
                                                                    if strcmp(j.getNodeName, 'expName') || ...
                                                                       strcmp(j.getNodeName, 'estType')
                                                                        tmp1 = string(j.getNodeName);
                                                                        tmp2 = string(j.getFirstChild.getNodeValue);
                                                                        detect.params = setfield(detect.params, tmp1{1}, tmp2{1});
                                                                    elseif strcmp(j.getNodeName, 'dx') || ...
                                                                            strcmp(j.getNodeName, 'densityTh') || ...
                                                                            strcmp(j.getNodeName, 'seedDist') || ...
                                                                            strcmp(j.getNodeName, 'maxSize') || ...
                                                                            strcmp(j.getNodeName, 'confEllPerc') || ...
                                                                            strcmp(j.getNodeName, 'minPtsTh') || ...
                                                                            strcmp(j.getNodeName, 'driftNptsTh') || ...
                                                                            strcmp(j.getNodeName, 'minCellsTh') || ...
                                                                            strcmp(j.getNodeName, 'angSimTh') || ...
                                                                            strcmp(j.getNodeName, 'sampledRatioTh') || ...
                                                                            strcmp(j.getNodeName, 'diffInWell') || ...
                                                                            strcmp(j.getNodeName, 'bestItPlus') || ...
                                                                            strcmp(j.getNodeName, 'correctField') || ...
                                                                            strcmp(j.getNodeName, 'dxMin') || ...
                                                                            strcmp(j.getNodeName, 'dxMax') || ...
                                                                            strcmp(j.getNodeName, 'dxStep')
                                                                        tmp = string(j.getNodeName);
                                                                        detect.params = setfield(detect.params, tmp{1}, str2num(string(j.getFirstChild.getNodeValue)));
                                                                    end
                                                                    j = j.getNextSibling;
                                                                end
                                                            end
                                                            i = i.getNextSibling;
                                                        end
                                                    elseif strcmp(h.getNodeName, 'value')
                                                        i = h.getFirstChild;
                                                        while ~isempty(i)
                                                            if strcmp(i.getNodeName, 'PotWellsWindows')
                                                                detect.well_wins = {};
                                                                j = i.getFirstChild;
                                                                while ~isempty(j)
                                                                    if strcmp(j.getNodeName, 'wins')
                                                                        wells = {};
                                                                        k = j.getFirstChild;
                                                                        while ~isempty(k)
                                                                            if strcmp(k.getNodeName, 'wells')
                                                                                well = struct();
                                                                                l = k.getFirstChild;
                                                                                while ~isempty(l)
                                                                                    if strcmp(l.getNodeName, 'ell')
                                                                                        well.ell = struct();
                                                                                        m = l.getFirstChild;
                                                                                        while ~isempty(m)
                                                                                            if strcmp(m.getNodeName, 'mu') || ...
                                                                                                    strcmp(m.getNodeName, 'rad')
                                                                                                tmp1 = string(m.getNodeName);
                                                                                                tmp2 = string(m.getFirstChild.getNodeValue);
                                                                                                well.ell = setfield(well.ell, tmp1{1}, sscanf(tmp2{1}, '%g %g')');
                                                                                            elseif strcmp(m.getNodeName, 'phi')
                                                                                                well.ell.phi = str2num(string(m.getFirstChild.getNodeValue));
                                                                                            end
                                                                                            m = m.getNextSibling;
                                                                                        end
                                                                                    elseif strcmp(l.getNodeName, 'A') || ...
                                                                                            strcmp(l.getNodeName, 'D') || ...
                                                                                            strcmp(l.getNodeName, 'score') || ...
                                                                                            strcmp(l.getNodeName, 'correctedD')
                                                                                        tmp = string(l.getNodeName);
                                                                                        well = setfield(well, tmp{1}, str2num(string(l.getFirstChild.getNodeValue)));
                                                                                    elseif strcmp(l.getNodeName, 'fitRes')
                                                                                        dat = {};
                                                                                        best_it = {};
                                                                                        m = l.getFirstChild;
                                                                                        while ~isempty(m)
                                                                                            if strcmp(m.getNodeName, 'traj_ids')
                                                                                                tmp = strsplit(string(m.getFirstChild.getNodeValue), '\n');
                                                                                                for uu=1:length(tmp)
                                                                                                    dat{uu} = strsplit(tmp{uu}, ' ');
                                                                                                    if isempty(dat{uu})
                                                                                                        dat{uu} = {};
                                                                                                    else
                                                                                                        dat{uu} = cellfun(@(x) str2num(x), dat{uu}(1:(end-1)));
                                                                                                    end
                                                                                                end
                                                                                            elseif strcmp(m.getNodeName, 'bestIt')
                                                                                                best_it = str2num(string(m.getFirstChild.getNodeValue));
                                                                                            end
                                                                                            m = m.getNextSibling;
                                                                                        end
                                                                                        
                                                                                        well.trajs_id = dat{best_it+1};
                                                                                    end

                                                                                    l = l.getNextSibling;
                                                                                end
                                                                                wells{length(wells)+1} = well;
                                                                            end
                                                                            k = k.getNextSibling;
                                                                        end
                                                                        detect.well_wins{length(detect.well_wins)+1} = wells;
                                                                    elseif strcmp(j.getNodeName, 'links')
                                                                        detect.fams = {};
                                                                        if isempty(j.getFirstChild)
                                                                            j = j.getNextSibling;
                                                                            continue
                                                                        end
                                                                        lines = strsplit(string(j.getFirstChild.getNodeValue), '\n');
                                                                        for idx=1:(length(lines)-1)
                                                                            tmp = sscanf(lines{idx}, '%d %d %d') + 1; %0 indexed array to 1 indexed array
                                                                            if tmp(1) > length(detect.fams)
                                                                                assert(tmp(1) == length(detect.fams) + 1);
                                                                                detect.fams{length(detect.fams)+1} = [];
                                                                            end
                                                                            detect.fams{tmp(1)} = [detect.fams{tmp(1)}; tmp(2:3)'];
                                                                        end
                                                                    elseif strcmp(j.getNodeName, 'linker')
                                                                        detect.fam_params = struct();
                                                                        k = j.getFirstChild;
                                                                        while ~isempty(k)
                                                                            if strcmp(k.getNodeName, 'maxFrameGap') || ...
                                                                               strcmp(k.getNodeName, 'maxDist')
                                                                                tmp = string(k.getNodeName);
                                                                                detect.fam_params = setfield(detect.fam_params, tmp{1}, ...
                                                                                    str2num(string(k.getFirstChild.getNodeValue)));
                                                                            end
                                                                            k = k.getNextSibling;
                                                                        end
                                                                    end
                                                                    j = j.getNextSibling;
                                                                end
                                                            end
                                                            i = i.getNextSibling;
                                                        end
                                                    end
                                                    h = h.getNextSibling;
                                                end
                                                algo.detects{length(algo.detects)+1} = detect;
                                            end
                                            g = g.getNextSibling;
                                        end
                                    end
                                    f = f.getNextSibling;
                                end
                                well_algos{length(well_algos)+1} = algo;
                            end
                            e = e.getNextSibling;
                        end
                        d = d.getNextSibling;
                    end
                end
                c = c.getNextSibling;
            end
        end

        function poly = loadGraphPolygon(e)
            assert(strcmp(e.getNodeName, 'MyPolygon'));
            poly = [];

            e1 = e.getFirstChild;
            while ~isempty(e1)
                if strcmp(e1.getNodeName, 'poly')
                    lines = strsplit(string(e1.getFirstChild.getNodeValue), '\n');
                    for i=2:(length(lines)-1)
                        poly = [poly; sscanf(lines{i}, '%g %g')'];
                    end
                end
                e1 = e1.getNextSibling;
            end
        end
        
        function nodes = loadGraphNodes(e)
            assert(strcmp(e.getNodeName, 'nodes'));
            nodes = {};

            e1 = PluginAdapter.xmlreadstring(e.getFirstChild.getNodeValue);
            e1 = e1.getFirstChild.getFirstChild;
            while ~isempty(e1)
                if strcmp(e1.getNodeName, 'entry')
                    e2 = e1.getFirstChild;
                    key = nan;
                    while ~isempty(e2)
                        if strcmp(e2.getNodeName, 'key')
                            key = str2num(string(e2.getFirstChild.getNodeValue));
                        elseif strcmp(e2.getNodeName, 'value')
                            e3 = e2.getFirstChild;
                            while ~isempty(e3)
                                if strcmp(e3.getNodeName, 'MyPolygon')
                                    nodes{key} = PluginAdapter.loadGraphPolygon(e3);
                                end
                                e3 = e3.getNextSibling;
                            end
                        end
                        e2 = e2.getNextSibling;
                    end
                end
                e1 = e1.getNextSibling;
            end
        end
        
        function C = loadGraphC(e, nNodes)
            assert(strcmp(e.getNodeName, 'C'));

            
            tmp = [];
            lines = strsplit(string(e.getFirstChild.getNodeValue), '\n');
            for k=1:(length(lines)-1)
                tmp = [tmp; sscanf(lines{k}, '%d %d %d')'];
            end

            tmp(tmp(:,1) == 0, 1) = nNodes + 1;
            tmp(tmp(:,2) == 0, 2) = nNodes + 1;
            
            C = sparse(tmp(:,1), tmp(:,2), tmp(:,3));
        end

        function graph_algos = loadGraphs(fname)
            a = xmlread(fname);

            xRoot = a.getDocumentElement;

            graph_algos = {};

            c = xRoot.getFirstChild;
            while ~isempty(c)
                if strcmp(c.getNodeName, 'graphs')
                    d = c.getFirstChild;
                    while ~isempty(d)
                        res = string(d.getNodeValue);
                        tmp = PluginAdapter.xmlreadstring(res{1});
                        e = tmp.getFirstChild.getFirstChild;
                        while ~isempty(e)
                            if strcmp(e.getNodeName, 'entry')
                                algo = struct();
                                f = e.getFirstChild;
                                while ~isempty(f)
                                    if strcmp(f.getNodeName, 'key')
                                        algo.name = string(f.getFirstChild.getNodeValue);
                                        algo.name = algo.name{1};
                                    elseif strcmp(f.getNodeName, 'value')
                                        algo.detects = {};
                                        g = f.getFirstChild;
                                        while ~isempty(g)
                                            if strcmp(g.getNodeName, 'entry')
                                                detect = struct();
                                                h = g.getFirstChild;
                                                while ~isempty(h)
                                                    if strcmp(h.getNodeName, 'key')
                                                        i = h.getFirstChild;
                                                        while ~isempty(i)
                                                            if ~strcmp(i.getNodeName, '#text')
                                                                detect.params = struct();
                                                                detect.params.type = string(i.getNodeName);
                                                                detect.params.type = detect.params.type{1};

                                                                j = i.getFirstChild;
                                                                while ~isempty(j)
                                                                    if strcmp(j.getNodeName, 'expName') || ...
                                                                       strcmp(j.getNodeName, 'nodeT')
                                                                        tmp1 = string(j.getNodeName);
                                                                        tmp2 = string(j.getFirstChild.getNodeValue);
                                                                        detect.params = setfield(detect.params, tmp1{1}, tmp2{1});
                                                                    elseif strcmp(j.getNodeName, 'lowVelTh') || ...
                                                                            strcmp(j.getNodeName, 'minArea') || ...
                                                                            strcmp(j.getNodeName, 'maxArea') || ...
                                                                            strcmp(j.getNodeName, 'minVolEllEps') || ...
                                                                            strcmp(j.getNodeName, 'maxClustNpts') || ...
                                                                            strcmp(j.getNodeName, 'RMax') || ...
                                                                            strcmp(j.getNodeName, 'RStep') || ...
                                                                            strcmp(j.getNodeName, 'RMin') || ...
                                                                            strcmp(j.getNodeName, 'NMin') || ...
                                                                            strcmp(j.getNodeName, 'NStep') || ...
                                                                            strcmp(j.getNodeName, 'NMax')
                                                                        tmp = string(j.getNodeName);
                                                                        detect.params = setfield(detect.params, tmp{1}, str2num(string(j.getFirstChild.getNodeValue)));
                                                                    end
                                                                    j = j.getNextSibling;
                                                                end
                                                            end
                                                            i = i.getNextSibling;
                                                        end
                                                    elseif strcmp(h.getNodeName, 'value')
                                                        i = h.getFirstChild;
                                                        while ~isempty(i)
                                                            if strcmp(i.getNodeName, 'GraphWindows')
                                                                detect.graph_wins = {};
                                                                j = i.getFirstChild;
                                                                while ~isempty(j)
                                                                    if strcmp(j.getNodeName, 'wins')
                                                                        graphs = {};
                                                                        k = j.getFirstChild;
                                                                        while ~isempty(k)
                                                                            cpt = 1;
                                                                            if strcmp(k.getNodeName, 'nodes')
                                                                                graphs{cpt} = struct();
                                                                                graphs{cpt}.nodes = PluginAdapter.loadGraphNodes(k);
                                                                            elseif strcmp(k.getNodeName, 'C')
                                                                                graphs{cpt}.C = PluginAdapter.loadGraphC(k, length(graphs{cpt}.nodes));
                                                                                cpt = cpt + 1;
                                                                            end
                                                                            k = k.getNextSibling;
                                                                        end
                                                                        detect.graph_wins{length(detect.graph_wins)+1} = graphs;
                                                                    end
                                                                    j = j.getNextSibling;
                                                                end
                                                            end
                                                            i = i.getNextSibling;
                                                        end
                                                    end
                                                    h = h.getNextSibling;
                                                end
                                                algo.detects{length(algo.detects)+1} = detect;
                                            end
                                            g = g.getNextSibling;
                                        end
                                    end
                                    f = f.getNextSibling;
                                end
                                graph_algos{length(graph_algos)+1} = algo;
                            end
                            e = e.getNextSibling;
                        end
                        d = d.getNextSibling;
                    end
                end
                c = c.getNextSibling;
            end
        end
        
        function [parseResult,p] = xmlreadstring(stringToParse,varargin)
            %XMLREADSTRING Modified XMLREAD function to read XML data from a string.
            % Author: Luis Cantero.
            % The MathWorks.

            p = locGetParser(varargin);
            locSetEntityResolver(p,varargin);
            locSetErrorHandler(p,varargin);

            % Parse and return.
            parseStringBuffer = java.io.StringBufferInputStream(stringToParse);
            parseResult = p.parse(parseStringBuffer);



            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            function p = locGetParser(args)
                p = [];
                for i=1:length(args)
                    if isa(args{i},'javax.xml.parsers.DocumentBuilderFactory')
                        javaMethod('setValidating',args{i},locIsValidating(args));
                        p = javaMethod('newDocumentBuilder',args{i});
                        break;
                    elseif isa(args{i},'javax.xml.parsers.DocumentBuilder')
                        p = args{i};
                        break;
                    end
                end

                if isempty(p)
                    parserFactory = javaMethod('newInstance',...
                        'javax.xml.parsers.DocumentBuilderFactory');

                    javaMethod('setValidating',parserFactory,locIsValidating(args));
                    p = javaMethod('newDocumentBuilder',parserFactory);
                end
            end

            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            function tf=locIsValidating(args)
                tf=any(strcmp(args,'-validating'));
            end

            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            function locSetEntityResolver(p,args)
                for i=1:length(args)
                    if isa(args{i},'org.xml.sax.EntityResolver')
                        p.setEntityResolver(args{i});
                        break;
                    end
                end
            end

            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            function locSetErrorHandler(p,args)
                for i=1:length(args)
                    if isa(args{i},'org.xml.sax.ErrorHandler')
                        p.setErrorHandler(args{i});
                        break;
                    end
                end
            end
        end
    end
end

