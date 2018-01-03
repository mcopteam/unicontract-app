

/**
 * Copyright (c) 2006-2012, JGraph Ltd
 */
Format = function(editorUi, container)
{
	this.editorUi = editorUi;
	this.container = container;
};

/**
 * Returns information about the current selection.
 */
Format.prototype.labelIndex = 0;

/**
 * Returns information about the current selection.
 */
Format.prototype.currentIndex = 0;

/**
 * Adds the label menu items to the given menu and parent.
 */
Format.prototype.init = function()
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	this.update = mxUtils.bind(this, function(sender, evt)
	{
		this.clearSelectionState();
		this.refresh();
	});
	
	graph.getSelectionModel().addListener(mxEvent.CHANGE, this.update);
	graph.addListener(mxEvent.EDITING_STARTED, this.update);
	graph.addListener(mxEvent.EDITING_STOPPED, this.update);
	graph.getModel().addListener(mxEvent.CHANGE, this.update);
	graph.addListener(mxEvent.ROOT, mxUtils.bind(this, function()
	{
		this.refresh();
	}));
	
	this.refresh();
};

/**
 * Returns information about the current selection.
 */
Format.prototype.clearSelectionState = function()
{
	this.selectionState = null;
};

/**
 * Returns information about the current selection.
 */
Format.prototype.getSelectionState = function()
{
	if (this.selectionState == null)
	{
		this.selectionState = this.createSelectionState();
	}
	
	return this.selectionState;
};

/**
 * Returns information about the current selection.
 */
Format.prototype.createSelectionState = function()
{
	var cells = this.editorUi.editor.graph.getSelectionCells();
	var result = this.initSelectionState();
	
	for (var i = 0; i < cells.length; i++)
	{
		this.updateSelectionStateForCell(result, cells[i], cells);
	}
	
	return result;
};

/**
 * Returns information about the current selection.
 */
Format.prototype.initSelectionState = function()
{
	return {vertices: [], edges: [], x: null, y: null, width: null, height: null, style: {},
		containsImage: false, containsLabel: false, fill: true, glass: true, rounded: true,
		comic: true, autoSize: false, image: true, shadow: true};
};

/**
 * Returns information about the current selection.
 */
Format.prototype.updateSelectionStateForCell = function(result, cell, cells)
{
	var graph = this.editorUi.editor.graph;
	
	if (graph.getModel().isVertex(cell))
	{
		result.vertices.push(cell);
		var geo = graph.getCellGeometry(cell);
		
		if (geo != null)
		{
			if (geo.width > 0)
			{
				if (result.width == null)
				{
					result.width = geo.width;
				}
				else if (result.width != geo.width)
				{
					result.width = '';
				}
			}
			else
			{
				result.containsLabel = true;
			}
			
			if (geo.height > 0)
			{
				if (result.height == null)
				{
					result.height = geo.height;
				}
				else if (result.height != geo.height)
				{
					result.height = '';
				}
			}
			else
			{
				result.containsLabel = true;
			}
			
			if (!geo.relative || geo.offset != null)
			{
				var x = (geo.relative) ? geo.offset.x : geo.x;
				var y = (geo.relative) ? geo.offset.y : geo.y;
				
				if (result.x == null)
				{
					result.x = x;
				}
				else if (result.x != x)
				{
					result.x = '';
				}
				
				if (result.y == null)
				{
					result.y = y;
				}
				else if (result.y != y)
				{
					result.y = '';
				}
			}
		}
	}
	else if (graph.getModel().isEdge(cell))
	{
		result.edges.push(cell);
	}

	var state = graph.view.getState(cell);
	
	if (state != null)
	{
		result.autoSize = result.autoSize || this.isAutoSizeState(state);
		result.glass = result.glass && this.isGlassState(state);
		result.rounded = result.rounded && this.isRoundedState(state);
		result.comic = result.comic && this.isComicState(state);
		result.image = result.image && this.isImageState(state);
		result.shadow = result.shadow && this.isShadowState(state);
		result.fill = result.fill && this.isFillState(state);
		
		var shape = mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null);
		result.containsImage = result.containsImage || shape == 'image';
		
		for (var key in state.style)
		{
			var value = state.style[key];
			
			if (value != null)
			{
				if (result.style[key] == null)
				{
					result.style[key] = value;
				}
				else if (result.style[key] != value)
				{
					result.style[key] = '';
				}
			}
		}
	}
};

/**
 * Returns information about the current selection.
 */
Format.prototype.isFillState = function(state)
{
	return state.view.graph.model.isVertex(state.cell) ||
		mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null) == 'arrow' ||
		mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null) == 'flexArrow';
};

/**
 * Returns information about the current selection.
 */
Format.prototype.isGlassState = function(state)
{
	var shape = mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null);
	
	return (shape == 'label' || shape == 'rectangle' || shape == 'internalStorage' ||
			shape == 'ext' || shape == 'umlLifeline' || shape == 'swimlane' ||
			shape == 'process');
};

/**
 * Returns information about the current selection.
 */
Format.prototype.isRoundedState = function(state)
{
	var shape = mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null);
	
	return (shape == 'label' || shape == 'rectangle' || shape == 'internalStorage' || shape == 'corner' ||
			shape == 'parallelogram' || shape == 'swimlane' || shape == 'triangle' || shape == 'trapezoid' ||
			shape == 'ext' || shape == 'step' || shape == 'tee' || shape == 'process' || shape == 'link' ||
			shape == 'rhombus' || shape == 'offPageConnector' || shape == 'loopLimit' || shape == 'hexagon' ||
			shape == 'manualInput' || shape == 'curlyBracket' || shape == 'singleArrow' ||
			shape == 'doubleArrow' || shape == 'flexArrow' || shape == 'card' || shape == 'umlLifeline');
};

/**
 * Returns information about the current selection.
 */
Format.prototype.isComicState = function(state)
{
	var shape = mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null);
	
	return mxUtils.indexOf(['label', 'rectangle', 'internalStorage', 'corner', 'parallelogram', 'note', 'collate',
	                        'swimlane', 'triangle', 'trapezoid', 'ext', 'step', 'tee', 'process', 'link', 'rhombus',
	                        'offPageConnector', 'loopLimit', 'hexagon', 'manualInput', 'singleArrow', 'doubleArrow',
	                        'flexArrow', 'card', 'umlLifeline', 'connector', 'folder', 'component', 'sortShape',
	                        'cross', 'umlFrame', 'cube', 'isoCube', 'isoRectangle'], shape) >= 0;
};

/**
 * Returns information about the current selection.
 */
Format.prototype.isAutoSizeState = function(state)
{
	return mxUtils.getValue(state.style, mxConstants.STYLE_AUTOSIZE, null) == '1';
};

/**
 * Returns information about the current selection.
 */
Format.prototype.isImageState = function(state)
{
	var shape = mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null);
	
	return (shape == 'label' || shape == 'image');
};

/**
 * Returns information about the current selection.
 */
Format.prototype.isShadowState = function(state)
{
	var shape = mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null);
	
	return (shape != 'image');
};

/**
 * Adds the label menu items to the given menu and parent.
 */
Format.prototype.clear = function()
{
	this.container.innerHTML = '';
	
	// Destroy existing panels
	if (this.panels != null)
	{
		for (var i = 0; i < this.panels.length; i++)
		{
			this.panels[i].destroy();
		}
	}
	
	this.panels = [];
};

/**
 * Adds the label menu items to the given menu and parent.
 */
Format.prototype.refresh = function()
{
	// Performance tweak: No refresh needed if not visible
	this.container.flag = false
	if (this.container.style.width == '0px')
	{
		return;
	}
	
	this.clear();
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	
	var div = document.createElement('div');
	div.style.whiteSpace = 'nowrap';
	div.style.color = 'rgb(112, 112, 112)';
	div.style.textAlign = 'left';
	div.style.cursor = 'default';
	
	var label = document.createElement('div');
	label.style.border = '1px solid #c0c0c0';
	label.style.borderWidth = '0px 0px 1px 0px';
	label.style.textAlign = 'center';
	label.style.fontWeight = 'bold';
	label.style.overflow = 'hidden';
	label.style.display = (mxClient.IS_QUIRKS) ? 'inline' : 'inline-block';
	label.style.paddingTop = '8px';
	label.style.height = (mxClient.IS_QUIRKS) ? '34px' : '25px';
	label.style.width = '100%';
	this.container.appendChild(div);
	
	if (graph.isSelectionEmpty())
	{
		// mxUtils.write(label, mxResources.get('diagram'));
		//
		// // Adds button to hide the format panel since
		// // people don't seem to find the toolbar button
		// // and the menu item in the format menu
		// var img = document.createElement('img');
		// img.setAttribute('border', '0');
		// img.setAttribute('src', Dialog.prototype.closeImage);
		// img.setAttribute('title', mxResources.get('hide'));
		// img.style.position = 'absolute';
		// img.style.display = 'block';
		// img.style.right = '0px';
		// img.style.top = '8px';
		// img.style.cursor = 'pointer';
		// img.style.marginTop = '1px';
		// img.style.marginRight = '17px';
		// img.style.border = '1px solid transparent';
		// img.style.padding = '1px';
		// img.style.opacity = 0.5;
		// label.appendChild(img)
		//
		// mxEvent.addListener(img, 'click', function()
		// {
		// 	ui.actions.get('formatPanel').funct();
		// });
		//
		// div.appendChild(label);
		// this.panels.push(new DiagramFormatPanel(this, ui, div));
	}
	else if (graph.isEditing())
	{
		mxUtils.write(label, mxResources.get('text'));
		div.appendChild(label);
		this.panels.push(new TextFormatPanel(this, ui, div));
	}
	else
	{
		var containsLabel = this.getSelectionState().containsLabel;
		var currentLabel = null;
		var currentPanel = null;
		
		var addClickHandler = mxUtils.bind(this, function(elt, panel, index)
		{
			var clickHandler = mxUtils.bind(this, function(evt)
			{
				if (currentLabel != elt)
				{
					if (containsLabel)
					{
						this.labelIndex = index;
					}
					else
					{
						this.currentIndex = index;
					}
					
					if (currentLabel != null)
					{
						currentLabel.style.backgroundColor = '#d7d7d7';
						currentLabel.style.borderBottomWidth = '1px';
					}
	
					currentLabel = elt;
					currentLabel.style.backgroundColor = '';
					currentLabel.style.borderBottomWidth = '0px';
					
					if (currentPanel != panel)
					{
						if (currentPanel != null)
						{
							currentPanel.style.display = 'none';
						}
						
						currentPanel = panel;
						currentPanel.style.display = '';
					}
				}
			});
			
			mxEvent.addListener(elt, 'click', clickHandler);
			
			if (index == ((containsLabel) ? this.labelIndex : this.currentIndex))
			{
				// Invokes handler directly as a workaround for no click on DIV in KHTML.
				clickHandler();
			}
		});
		
		var idx = 0;

		label.style.backgroundColor = '#d7d7d7';
		label.style.borderLeftWidth = '1px';
		// label.style.width = (containsLabel) ? '50%' : '50%';
		label.style.width = (containsLabel) ? '100%' : '100%';
		// label.style.width = (containsLabel) ? '50%' : '33.3%';
		// label.style.width = (containsLabel) ? '50%' : '33.3%';
		// label.style.width = (containsLabel) ? '100%' : '100%';
		// label.style.width = (containsLabel) ? '100%' : '100%';
		var label2 = label.cloneNode(false);
		var label3 = label2.cloneNode(false);

		// Workaround for ignored background in IE
		label2.style.backgroundColor = '#d7d7d7';
		label3.style.backgroundColor = '#d7d7d7';
		
		// Style
		if (containsLabel)
		{
			label2.style.borderLeftWidth = '0px';
		}
		else
		{
			label.style.borderLeftWidth = '0px';
			mxUtils.write(label, mxResources.get('style'));
			div.appendChild(label);
			
			var stylePanel = div.cloneNode(false);
			stylePanel.style.display = 'none';
			this.panels.push(new StyleFormatPanel(this, ui, stylePanel));
			this.container.appendChild(stylePanel);
// 样式去掉
			// addClickHandler(label, stylePanel, idx++);
		}
		
		// Text
//		mxUtils.write(label2, mxResources.get('text'));
//		// div.appendChild(label2);
//
//		var textPanel = div.cloneNode(false);
//		textPanel.style.display = 'none';
//		this.panels.push(new TextFormatPanel(this, ui, textPanel));
//		this.container.appendChild(textPanel);
		
		// Arrange
		mxUtils.write(label3, mxResources.get('arrange'));
		div.appendChild(label3);

		var arrangePanel = div.cloneNode(false);
		arrangePanel.style.display = 'none';
		this.panels.push(new ArrangePanel(this, ui, arrangePanel));
		if(!this.container.flag){
            this.container.appendChild(arrangePanel);
            this.container.flag = true
		}

		
//		addClickHandler(label2, textPanel, idx++);
		addClickHandler(label3, arrangePanel, idx++);
	}
};

/**
 * Base class for format panels.
 */
BaseFormatPanel = function(format, editorUi, container)
{
	this.format = format;
	this.editorUi = editorUi;
	this.container = container;
	this.listeners = [];
};

/**
 * Adds the given color option.
 */
BaseFormatPanel.prototype.getSelectionState = function()
{
	var graph = this.editorUi.editor.graph;
	var cells = graph.getSelectionCells();
	var shape = null;

	for (var i = 0; i < cells.length; i++)
	{
		var state = graph.view.getState(cells[i]);
		
		if (state != null)
		{
			var tmp = mxUtils.getValue(state.style, mxConstants.STYLE_SHAPE, null);
			
			if (tmp != null)
			{
				if (shape == null)
				{
					shape = tmp;
				}
				else if (shape != tmp)
				{
					return null;
				}
			}
			
		}
	}
	
	return shape;
};

/**
 * Install input handler.
 */
BaseFormatPanel.prototype.installInputHandler = function(input, key, defaultValue, min, max, unit, textEditFallback, isFloat)
{
	unit = (unit != null) ? unit : '';
	isFloat = (isFloat != null) ? isFloat : false;
	
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	
	min = (min != null) ? min : 1;
	max = (max != null) ? max : 999;
	
	var selState = null;
	var updating = false;
	
	var update = mxUtils.bind(this, function(evt)
	{
		var value = (isFloat) ? parseFloat(input.value) : parseInt(input.value);

		// Special case: angle mod 360
		if (!isNaN(value) && key == mxConstants.STYLE_ROTATION)
		{
			// Workaround for decimal rounding errors in floats is to
			// use integer and round all numbers to two decimal point
			value = mxUtils.mod(Math.round(value * 100), 36000) / 100;
		}
		
		value = Math.min(max, Math.max(min, (isNaN(value)) ? defaultValue : value));
		
		if (graph.cellEditor.isContentEditing() && textEditFallback)
		{
			if (!updating)
			{
				updating = true;
				
				if (selState != null)
				{
					graph.cellEditor.restoreSelection(selState);
					selState = null;
				}
				
				textEditFallback(value);
				input.value = value + unit;
	
				// Restore focus and selection in input
				updating = false;
			}
		}
		else if (value != mxUtils.getValue(this.format.getSelectionState().style, key, defaultValue))
		{
			if (graph.isEditing())
			{
				graph.stopEditing(true);
			}
			
			graph.getModel().beginUpdate();
			try
			{
				graph.setCellStyles(key, value, graph.getSelectionCells());
				
				// Handles special case for fontSize where HTML labels are parsed and updated
				if (key == mxConstants.STYLE_FONTSIZE)
				{
					var cells = graph.getSelectionCells();
					
					for (var i = 0; i < cells.length; i++)
					{
						var cell = cells[i];
							
						// Changes font tags inside HTML labels
						if (graph.isHtmlLabel(cell))
						{
							var div = document.createElement('div');
							div.innerHTML = graph.convertValueToString(cell);
							var elts = div.getElementsByTagName('font');
							
							for (var j = 0; j < elts.length; j++)
							{
								elts[j].removeAttribute('size');
								elts[j].style.fontSize = value + 'px';
							}
							
							graph.cellLabelChanged(cell, div.innerHTML)
						}
					}
				}
			}
			finally
			{
				graph.getModel().endUpdate();
			}
			
			ui.fireEvent(new mxEventObject('styleChanged', 'keys', [key],
					'values', [value], 'cells', graph.getSelectionCells()));
		}
		
		input.value = value + unit;
		mxEvent.consume(evt);
	});

	if (textEditFallback && graph.cellEditor.isContentEditing())
	{
		// KNOWN: Arrow up/down clear selection text in quirks/IE 8
		// Text size via arrow button limits to 16 in IE11. Why?
		mxEvent.addListener(input, 'mousedown', function()
		{
			selState = graph.cellEditor.saveSelection();
		});
		
		mxEvent.addListener(input, 'touchstart', function()
		{
			selState = graph.cellEditor.saveSelection();
		});
	}
	
	mxEvent.addListener(input, 'change', update);
	mxEvent.addListener(input, 'blur', update);
	
	return update;
};

/**
 * Adds the given option.
 */
BaseFormatPanel.prototype.createPanel = function()
{
	var div = document.createElement('div');
	div.style.padding = '12px 0px 12px 18px';
	div.style.borderBottom = '1px solid #c0c0c0';
	
	return div;
};

/**
 * Adds the given option.
 */
BaseFormatPanel.prototype.createTitle = function(title)
{
	var div = document.createElement('div');
	div.style.padding = '0px 0px 6px 0px';
	div.style.whiteSpace = 'nowrap';
	div.style.overflow = 'hidden';
	div.style.width = '200px';
	div.style.fontWeight = 'bold';
	mxUtils.write(div, title);
	
	return div;
};

/**
 * 
 */
BaseFormatPanel.prototype.createStepper = function(input, update, step, height, disableFocus, defaultValue)
{
	step = (step != null) ? step : 1;
	height = (height != null) ? height : 8;
	
	if (mxClient.IS_QUIRKS)
	{
		height = height - 2;
	}
	else if (mxClient.IS_MT || document.documentMode >= 8)
	{
		height = height + 1;
	} 
	
	var stepper = document.createElement('div');
	mxUtils.setPrefixedStyle(stepper.style, 'borderRadius', '3px');
	stepper.style.border = '1px solid rgb(192, 192, 192)';
	stepper.style.position = 'absolute';
	
	var up = document.createElement('div');
	up.style.borderBottom = '1px solid rgb(192, 192, 192)';
	up.style.position = 'relative';
	up.style.height = height + 'px';
	up.style.width = '10px';
	up.className = 'geBtnUp';
	stepper.appendChild(up);
	
	var down = up.cloneNode(false);
	down.style.border = 'none';
	down.style.height = height + 'px';
	down.className = 'geBtnDown';
	stepper.appendChild(down);

	mxEvent.addListener(down, 'click', function(evt)
	{
		if (input.value == '')
		{
			input.value = defaultValue || '2';
		}
		
		var val = parseInt(input.value);
		
		if (!isNaN(val))
		{
			input.value = val - step;
			
			if (update != null)
			{
				update(evt);
			}
		}
		
		mxEvent.consume(evt);
	});
	
	mxEvent.addListener(up, 'click', function(evt)
	{
		if (input.value == '')
		{
			input.value = defaultValue || '0';
		}
		
		var val = parseInt(input.value);
		
		if (!isNaN(val))
		{
			input.value = val + step;
			
			if (update != null)
			{
				update(evt);
			}
		}
		
		mxEvent.consume(evt);
	});
	
	// Disables transfer of focus to DIV but also :active CSS
	// so it's only used for fontSize where the focus should
	// stay on the selected text, but not for any other input.
	if (disableFocus)
	{
		var currentSelection = null;
		
		mxEvent.addGestureListeners(stepper,
			function(evt)
			{
				// Workaround for lost current selection in page because of focus in IE
				if (mxClient.IS_QUIRKS || document.documentMode == 8)
				{
					currentSelection = document.selection.createRange();
				}
				
				mxEvent.consume(evt);
			},
			null,
			function(evt)
			{
				// Workaround for lost current selection in page because of focus in IE
				if (currentSelection != null)
				{
					try
					{
						currentSelection.select();
					}
					catch (e)
					{
						// ignore
					}
					
					currentSelection = null;
					mxEvent.consume(evt);
				}
			}
		);
	}
	
	return stepper;
};

/**
 * Adds the given option.
 */
BaseFormatPanel.prototype.createOption = function(label, isCheckedFn, setCheckedFn, listener)
{
	var div = document.createElement('div');
	div.style.padding = '6px 0px 1px 0px';
	div.style.whiteSpace = 'nowrap';
	div.style.overflow = 'hidden';
	div.style.width = '200px';
	div.style.height = (mxClient.IS_QUIRKS) ? '27px' : '18px';
	
	var cb = document.createElement('input');
	cb.setAttribute('type', 'checkbox');
	cb.style.margin = '0px 6px 0px 0px';
	div.appendChild(cb);

	var span = document.createElement('span');
	mxUtils.write(span, label);
	div.appendChild(span);

	var applying = false;
	var value = isCheckedFn();
	
	var apply = function(newValue)
	{
		if (!applying)
		{
			applying = true;
			
			if (newValue)
			{
				cb.setAttribute('checked', 'checked');
				cb.defaultChecked = true;
				cb.checked = true;
			}
			else
			{
				cb.removeAttribute('checked');
				cb.defaultChecked = false;
				cb.checked = false;
			}
			
			if (value != newValue)
			{
				value = newValue;
				
				// Checks if the color value needs to be updated in the model
				if (isCheckedFn() != value)
				{
					setCheckedFn(value);
				}
			}
			
			applying = false;
		}
	};

	mxEvent.addListener(div, 'click', function(evt)
	{
		// Toggles checkbox state for click on label
		var source = mxEvent.getSource(evt);
		
		if (source == div || source == span)
		{
			cb.checked = !cb.checked;
		}
		
		apply(cb.checked);
	});
	
	apply(value);
	
	if (listener != null)
	{
		listener.install(apply);
		this.listeners.push(listener);
	}

	return div;
};

/**
 * The string 'null' means use null in values.
 */
BaseFormatPanel.prototype.createCellOption = function(label, key, defaultValue, enabledValue, disabledValue, fn, action, stopEditing)
{
	enabledValue = (enabledValue != null) ? ((enabledValue == 'null') ? null : enabledValue) : '1';
	disabledValue = (disabledValue != null) ? ((disabledValue == 'null') ? null : disabledValue) : '0';
	
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	
	return this.createOption(label, function()
	{
		// Seems to be null sometimes, not sure why...
		var state = graph.view.getState(graph.getSelectionCell());
		
		if (state != null)
		{
			return mxUtils.getValue(state.style, key, defaultValue) != disabledValue;
		}
		
		return null;
	}, function(checked)
	{
		if (stopEditing)
		{
			graph.stopEditing();
		}
		
		if (action != null)
		{
			action.funct();
		}
		else
		{
			graph.getModel().beginUpdate();
			try
			{
				var value = (checked) ? enabledValue : disabledValue;
				graph.setCellStyles(key, value, graph.getSelectionCells());
				
				if (fn != null)
				{
					fn(graph.getSelectionCells(), value);
				}
				
				ui.fireEvent(new mxEventObject('styleChanged', 'keys', [key],
					'values', [value], 'cells', graph.getSelectionCells()));
			}
			finally
			{
				graph.getModel().endUpdate();
			}
		}
	},
	{
		install: function(apply)
		{
			this.listener = function()
			{
				// Seems to be null sometimes, not sure why...
				var state = graph.view.getState(graph.getSelectionCell());
				
				if (state != null)
				{
					apply(mxUtils.getValue(state.style, key, defaultValue) != disabledValue);
				}
			};
			
			graph.getModel().addListener(mxEvent.CHANGE, this.listener);
		},
		destroy: function()
		{
			graph.getModel().removeListener(this.listener);
		}
	});
};

/**
 * Adds the given color option.
 */
BaseFormatPanel.prototype.createColorOption = function(label, getColorFn, setColorFn, defaultColor, listener, callbackFn, hideCheckbox)
{
	var div = document.createElement('div');
	div.style.padding = '6px 0px 1px 0px';
	div.style.whiteSpace = 'nowrap';
	div.style.overflow = 'hidden';
	div.style.width = '200px';
	div.style.height = (mxClient.IS_QUIRKS) ? '27px' : '18px';
	
	var cb = document.createElement('input');
	cb.setAttribute('type', 'checkbox');
	cb.style.margin = '0px 6px 0px 0px';
	
	if (!hideCheckbox)
	{
		div.appendChild(cb);	
	}

	var span = document.createElement('span');
	mxUtils.write(span, label);
	div.appendChild(span);
	
	var applying = false;
	var value = getColorFn();

	var btn = null;

	var apply = function(color, disableUpdate)
	{
		if (!applying)
		{
			applying = true;
			btn.innerHTML = '<div style="width:' + ((mxClient.IS_QUIRKS) ? '30' : '36') +
				'px;height:12px;margin:3px;border:1px solid black;background-color:' +
				((color != null && color != mxConstants.NONE) ? color : defaultColor) + ';"></div>';
			
			// Fine-tuning in Firefox, quirks mode and IE8 standards
			if (mxClient.IS_MT || mxClient.IS_QUIRKS || document.documentMode == 8)
			{
				btn.firstChild.style.margin = '0px';
			}
			
			if (color != null && color != mxConstants.NONE)
			{
				cb.setAttribute('checked', 'checked');
				cb.defaultChecked = true;
				cb.checked = true;
			}
			else
			{
				cb.removeAttribute('checked');
				cb.defaultChecked = false;
				cb.checked = false;
			}
	
			btn.style.display = (cb.checked || hideCheckbox) ? '' : 'none';

			if (callbackFn != null)
			{
				callbackFn(color);
			}

			if (!disableUpdate && (hideCheckbox || value != color))
			{
				value = color;
				
				// Checks if the color value needs to be updated in the model
				if (hideCheckbox || getColorFn() != value)
				{
					setColorFn(value);
				}
			}
			
			applying = false;
		}
	};

	btn = mxUtils.button('', mxUtils.bind(this, function(evt)
	{
		this.editorUi.pickColor(value, apply);
		mxEvent.consume(evt);
	}));
	
	btn.style.position = 'absolute';
	btn.style.marginTop = '-4px';
	btn.style.right = (mxClient.IS_QUIRKS) ? '0px' : '20px';
	btn.style.height = '22px';
	btn.className = 'geColorBtn';
	btn.style.display = (cb.checked || hideCheckbox) ? '' : 'none';
	div.appendChild(btn);

	mxEvent.addListener(div, 'click', function(evt)
	{
		var source = mxEvent.getSource(evt);
		
		if (source == cb || source.nodeName != 'INPUT')
		{		
			// Toggles checkbox state for click on label
			if (source != cb)
			{
				cb.checked = !cb.checked;
			}
	
			// Overrides default value with current value to make it easier
			// to restore previous value if the checkbox is clicked twice
			if (!cb.checked && value != null && value != mxConstants.NONE &&
				defaultColor != mxConstants.NONE)
			{
				defaultColor = value;
			}
			
			apply((cb.checked) ? defaultColor : mxConstants.NONE);
		}
	});
	
	apply(value, true);
	
	if (listener != null)
	{
		listener.install(apply);
		this.listeners.push(listener);
	}
	
	return div;
};

/**
 * 
 */
BaseFormatPanel.prototype.createCellColorOption = function(label, colorKey, defaultColor, callbackFn, setStyleFn)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	
	return this.createColorOption(label, function()
	{
		// Seems to be null sometimes, not sure why...
		var state = graph.view.getState(graph.getSelectionCell());
		
		if (state != null)
		{
			return mxUtils.getValue(state.style, colorKey, null);
		}
		
		return null;
	}, function(color)
	{
		graph.getModel().beginUpdate();
		try
		{
			if (setStyleFn != null)
			{
				setStyleFn(color);
			}
			
			graph.setCellStyles(colorKey, color, graph.getSelectionCells());
			ui.fireEvent(new mxEventObject('styleChanged', 'keys', [colorKey],
				'values', [color], 'cells', graph.getSelectionCells()));
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}, defaultColor || mxConstants.NONE,
	{
		install: function(apply)
		{
			this.listener = function()
			{
				// Seems to be null sometimes, not sure why...
				var state = graph.view.getState(graph.getSelectionCell());
				
				if (state != null)
				{
					apply(mxUtils.getValue(state.style, colorKey, null));
				}
			};
			
			graph.getModel().addListener(mxEvent.CHANGE, this.listener);
		},
		destroy: function()
		{
			graph.getModel().removeListener(this.listener);
		}
	}, callbackFn);
};

/**
 * 
 */
BaseFormatPanel.prototype.addArrow = function(elt, height)
{
	height = (height != null) ? height : 10;
	
	var arrow = document.createElement('div');
	arrow.style.display = (mxClient.IS_QUIRKS) ? 'inline' : 'inline-block';
	arrow.style.padding = '6px';
	arrow.style.paddingRight = '4px';
	
	var m = (10 - height);
	
	if (m == 2)
	{
		arrow.style.paddingTop = 6 + 'px';
	}
	else if (m > 0)
	{
		arrow.style.paddingTop = (6 - m) + 'px';
	}
	else
	{
		arrow.style.marginTop = '-2px';
	}
	
	arrow.style.height = height + 'px';
	arrow.style.borderLeft = '1px solid #a0a0a0';
	arrow.innerHTML = '<img border="0" src="' + ((mxClient.IS_SVG) ? 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAHBJREFUeNpidHB2ZyAGsACxDRBPIKCuA6TwCBB/h2rABu4A8SYmKCcXiP/iUFgAxL9gCi8A8SwsirZCMQMTkmANEH9E4v+CmsaArvAdyNFI/FlQ92EoBIE+qCRIUz168DBgsU4OqhinQpgHMABAgAEALY4XLIsJ20oAAAAASUVORK5CYII=' :
		IMAGE_PATH + '/dropdown.png') + '" style="margin-bottom:4px;">';
	mxUtils.setOpacity(arrow, 70);
	
	var symbol = elt.getElementsByTagName('div')[0];
	
	if (symbol != null)
	{
		symbol.style.paddingRight = '6px';
		symbol.style.marginLeft = '4px';
		symbol.style.marginTop = '-1px';
		symbol.style.display = (mxClient.IS_QUIRKS) ? 'inline' : 'inline-block';
		mxUtils.setOpacity(symbol, 60);
	}

	mxUtils.setOpacity(elt, 100);
	elt.style.border = '1px solid #a0a0a0';
	elt.style.backgroundColor = 'white';
	elt.style.backgroundImage = 'none';
	elt.style.width = 'auto';
	elt.className += ' geColorBtn';
	mxUtils.setPrefixedStyle(elt.style, 'borderRadius', '3px');
	
	elt.appendChild(arrow);
	
	return symbol;
};

/**
 * 
 */
BaseFormatPanel.prototype.addUnitInput = function(container, unit, right, width, update, step, marginTop, disableFocus)
{
	marginTop = (marginTop != null) ? marginTop : 0;
	
	var input = document.createElement('input');
	input.style.position = 'absolute';
	input.style.textAlign = 'right';
	input.style.marginTop = '-2px';
	input.style.right = (right + 12) + 'px';
	input.style.width = width + 'px';
	container.appendChild(input);
	
	var stepper = this.createStepper(input, update, step, null, disableFocus);
	stepper.style.marginTop = (marginTop - 2) + 'px';
	stepper.style.right = right + 'px';
	container.appendChild(stepper);

	return input;
};

/**
 * 
 */
BaseFormatPanel.prototype.createRelativeOption = function(label, key, width, handler, init)
{
	width = (width != null) ? width : 44;
	
	var graph = this.editorUi.editor.graph;
	var div = this.createPanel();
	div.style.paddingTop = '10px';
	div.style.paddingBottom = '10px';
	mxUtils.write(div, label);
	div.style.fontWeight = 'bold';
	
	function update(evt)
	{
		if (handler != null)
		{
			handler(input);
		}
		else
		{
			var value = parseInt(input.value);
			value = Math.min(100, Math.max(0, (isNaN(value)) ? 100 : value));
			var state = graph.view.getState(graph.getSelectionCell());
			
			if (state != null && value != mxUtils.getValue(state.style, key, 100))
			{
				// Removes entry in style (assumes 100 is default for relative values)
				if (value == 100)
				{
					value = null;
				}
				
				graph.setCellStyles(key, value, graph.getSelectionCells());
			}
	
			input.value = ((value != null) ? value : '100') + ' %';
		}
		
		mxEvent.consume(evt);
	};

	var input = this.addUnitInput(div, '%', 20, width, update, 10, -15, handler != null);

	if (key != null)
	{
		var listener = mxUtils.bind(this, function(sender, evt, force)
		{
			if (force || input != document.activeElement)
			{
				var ss = this.format.getSelectionState();
				var tmp = parseInt(mxUtils.getValue(ss.style, key, 100));
				input.value = (isNaN(tmp)) ? '' : tmp + ' %';
			}
		});
		
		mxEvent.addListener(input, 'keydown', function(e)
		{
			if (e.keyCode == 13)
			{
				graph.container.focus();
				mxEvent.consume(e);
			}
			else if (e.keyCode == 27)
			{
				listener(null, null, true);
				graph.container.focus();
				mxEvent.consume(e);
			}
		});
		
		graph.getModel().addListener(mxEvent.CHANGE, listener);
		this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
		listener();
	}

	mxEvent.addListener(input, 'blur', update);
	mxEvent.addListener(input, 'change', update);
	
	if (init != null)
	{
		init(input);
	}

	return div;
};

/**
 * 
 */
BaseFormatPanel.prototype.addLabel = function(div, title, right, width)
{
	width = (width != null) ? width : 61;
	
	var label = document.createElement('div');
	mxUtils.write(label, title);
	label.style.position = 'absolute';
	label.style.right = right + 'px';
	label.style.width = width + 'px';
	label.style.marginTop = '6px';
	label.style.textAlign = 'center';
	div.appendChild(label);
};

/**
 * 
 */
BaseFormatPanel.prototype.addKeyHandler = function(input, listener)
{
	mxEvent.addListener(input, 'keydown', mxUtils.bind(this, function(e)
	{
		if (e.keyCode == 13)
		{
			this.editorUi.editor.graph.container.focus();
			mxEvent.consume(e);
		}
		else if (e.keyCode == 27)
		{
			if (listener != null)
			{
				listener(null, null, true);				
			}

			this.editorUi.editor.graph.container.focus();
			mxEvent.consume(e);
		}
	}));
};

/**
 * 
 */
BaseFormatPanel.prototype.styleButtons = function(elts)
{
	for (var i = 0; i < elts.length; i++)
	{
		mxUtils.setPrefixedStyle(elts[i].style, 'borderRadius', '3px');
		mxUtils.setOpacity(elts[i], 100);
		elts[i].style.border = '1px solid #a0a0a0';
		elts[i].style.padding = '4px';
		elts[i].style.paddingTop = '3px';
		elts[i].style.paddingRight = '1px';
		elts[i].style.margin = '1px';
		elts[i].style.width = '24px';
		elts[i].style.height = '20px';
		elts[i].className += ' geColorBtn';
	}
};

/**
 * Adds the label menu items to the given menu and parent.
 */
BaseFormatPanel.prototype.destroy = function()
{
	if (this.listeners != null)
	{
		for (var i = 0; i < this.listeners.length; i++)
		{
			this.listeners[i].destroy();
		}
		
		this.listeners = null;
	}
};

/**
 * Adds the label menu items to the given menu and parent.
 */
ArrangePanel = function(format, editorUi, container)
{
	BaseFormatPanel.call(this, format, editorUi, container);

	this.init();
//	this.container.removeChild(this.container.children[1]);
	
};

mxUtils.extend(ArrangePanel, BaseFormatPanel);

// debugger;
// mxEvent.addListener(ArrangePanel, 'click', function()
// {
// 	console.log("clickthis")
// });

/**
 * Adds the label menu items to the given menu and parent.
 */
ArrangePanel.prototype.init = function()
{
	var graph = this.editorUi.editor.graph;
	var ss = this.format.getSelectionState();
    var cells = graph.getSelectionCells();
	if (cells != null)
	{
		for (var i = 0; i < cells.length; i++) {
			var cell = cells[i];
            this.addBaseArrange(this.container,cell);
		}
	}
    var a = this.createPanel()
    // var d = this.addGroupOps(a)
    // this.container.appendChild(d);
};

/**
 *
 */

ArrangePanel.prototype.addProperty = function (attr,value) {
    this.editorUi.actions.get('changeCellValue').funct(attr,value);
};
ArrangePanel.prototype.removeProperty = function (attr) {
    this.editorUi.actions.get('removeAttribute').funct(attr);
};

// 属性
ArrangePanel.prototype.addBaseArrange = function(container,cell){
    // console.info('addBaseArrange')
    // debugger;
	var value = cell.getTypeId();
    var div = this.createPanel();
    if (!mxUtils.isNode(value)&& ((typeof cell.value)!='object')  && cell.edge != 1)
    {
        var doc = mxUtils.createXmlDocument();
        var obj = doc.createElement('object');
        switch (value){
            case 'Decision':
                // debugger
                obj.setAttribute('label', '决策');
                cell.setTypeId('Decision');
                obj.setAttribute('Ctype', 'Component_Task.Task_Decision' || '');
                obj.setAttribute('PreCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('DiscardCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('CompleteCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                break;
            case 'Enquiry':
                obj.setAttribute('label', '查询');
                cell.setTypeId('Enquiry');
                obj.setAttribute('Ctype', 'Component_Task.Task_Enquiry' || '');
                obj.setAttribute('PreCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('DiscardCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('CompleteCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('DataValueSetterExpressionList_Ctype', 'Component_Expression.Expression_Function' || '');
                obj.setAttribute('DataList_Ctype', 'Component_Data' || '');
                break;
            case 'Action':
                obj.setAttribute('label', '动作');
                cell.setTypeId('Action');
                obj.setAttribute('Ctype', 'Component_Task.Task_Action' || '');
                obj.setAttribute('PreCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('DiscardCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('CompleteCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('DataList_Ctype', 'Component_Data' || '');
                break;
            case 'Plan':
                obj.setAttribute('label', '计划');
                cell.setTypeId('Plan');
                obj.setAttribute('Ctype', 'Component_Task.Task_Plan' || '');
                obj.setAttribute('PreCondition_Ctype', ' Component_Expression.Expression_Condition' || '');
                obj.setAttribute('DiscardCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                obj.setAttribute('CompleteCondition_Ctype', 'Component_Expression.Expression_Condition' || '');
                break;
            default:
                break;
        }
        var ui = this.editorUi;
        var graph = ui.editor.graph;
        graph.getModel().setValue(cell, obj);
        // container.appendChild(div);
    }
	var that = this;
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	var rect = this.format.getSelectionState();
	div.style.paddingBottom = '8px';
	div.style.whiteSpace = 'normal';
	var labels=graph.getSelectionCell().getAttribute('label');
	var describe=graph.getSelectionCell().getAttribute('describe');
	var str=cell.style;
	var st=str[str.length-1];

    var divTop1 = document.createElement('div');
    var btn1 = createSpan("基本属性")
    btn1.style.fontSize='14px';
    btn1.style.fontWeight='bold';
    divTop1.appendChild(btn1);
    div.appendChild(divTop1);

    var lineTop=document.createElement('div');
    lineTop.style.height='1px';
    lineTop.style.width='100%';
    lineTop.style.marginTop='5px';
    lineTop.style.background='#c0c0c0';
    div.appendChild(lineTop)

    var divTop = document.createElement('div');
    var btn1 = createSpan("名称")
	btn1.style.fontWeight='bold';
	btn1.style.marginTop='3px';
    divTop.appendChild(btn1);

    var btn2 = createSpan("属性值");
    btn2.style.fontWeight='bold';
    btn2.style.marginLeft='40px';
    btn2.style.marginTop='3px';
    divTop.appendChild(btn2);
    div.appendChild(divTop);

    var lineTop=document.createElement('div');
    lineTop.style.height='1px';
    lineTop.style.width='100%';
    lineTop.style.background='#c0c0c0';
    div.appendChild(lineTop)
	switch (st){
		case '0'://开始节点
            var span3 = createSpan("标识")
			var xing=document.createElement('span');
			xing.innerHTML='*';
			xing.style.fontSize='14px';
			xing.style.marginLeft='5px';
			xing.style.color='red';
			span3.appendChild(xing);
            var textarea3=createTextarea(function(evt)
            {
                var value =textarea3.value;
                that.addProperty("Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'Cname',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea3,vali)
                }
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea3,"Cname");
            var divLine3=document.createElement('div');
            div.appendChild(span3);
            div.appendChild(textarea3);

			var name = sessionStorage.name;
			var pubkey = sessionStorage.pubkey;
            cell.setTypeId('Start');
			var span1 = createSpan("名称");
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span1.appendChild(xing);
			var textarea1=createTextarea(function(evt)
			{
				var value =textarea1.value;
				that.addProperty("Caption",value);
				that.addProperty("Creator",pubkey);
                var vali = validateCommonCell(cell.getTypeId(),'Caption',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea1,vali)
				}
			});
			applyDefalutValue(graph,textarea1,"Caption");
			div.appendChild(span1);
			div.appendChild(textarea1);

			var span2 = createSpan("描述");
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span2.appendChild(xing);
			var textarea2=createText(function(evt)
			{
				var value =textarea2.value;
				that.addProperty("Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'Description',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea2,vali)
                }
			});
			applyDefalutValue(graph,textarea2,"Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span2.style.float='left';
			span2.style.height='38px';
			span2.style.lineHeight='38px';
			span2.style.marginTop='2px';
			divLine.appendChild(span2);
			divLine.appendChild(textarea2);
			div.appendChild(divLine);

			var span4 = createSpan("创建者")

            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span4.appendChild(xing);
			var textarea4=createTextarea(function(evt)
			{
//				var value =textarea4.value;
//				that.addProperty("Creator",value);
			});
            textarea4.value=name;
            textarea4.style.marginTop='20px';
			textarea4.disabled='disabled';
//			applyDefalutValue(graph,textarea4,"Creator");
			div.appendChild(span4);
			div.appendChild(textarea4);

			var span5 = createSpan("创建时间");

            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span5.appendChild(xing);
			var textarea5=createTextarea1();
//			textarea5.setAttribute('id','layui-laydate-input5');
//			textarea5.onclick=function(){
//				var start = {
//					min: laydate.now()
//					,max: '2099-06-16 23:59:59'
//					,istoday: true
//					,choose: function(datas){
//						var value=datas;
//						that.addProperty('CreateTime',value)
//					},
//					istime: true,
//					format: 'YYYY-MM-DD hh:mm:ss'
//				};
//				start.elem = this;
//				laydate(start);
//
//			}
			var timestamp = Date.parse(new Date());
			var value=laydate.now(timestamp,'YYYY-MM-DD hh:mm:ss');
			textarea5.value=value;
			textarea5.disabled='disabled';
			that.addProperty('CreateTime',value)
			applyDefalutValue(graph,textarea5,"CreateTime");
			div.appendChild(span5);
			div.appendChild(textarea5);

			var span6 = createSpan("执行时间")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span6.appendChild(xing);
			var textarea6=createTextarea1();
			textarea6.setAttribute('id','layui-laydate-input6');
			textarea6.onclick=function(){
				var start = {
					min: laydate.now()
					,max: '2099-06-16 23:59:59'
					,istoday: false
					,choose: function(datas){
						var value=datas;
						that.addProperty('StartTime',value)
					},
					istime: true,
					format: 'YYYY-MM-DD hh:mm:ss'
				};
				start.elem = this;
				laydate(start);
			}
			applyDefalutValue(graph,textarea6,"StartTime");
			div.appendChild(span6);
			div.appendChild(textarea6);

			var span61 = createSpan("终止时间")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span61.appendChild(xing);
			var textarea61=createTextarea1();
			textarea61.setAttribute('id','layui-laydate-input61');
			textarea61.onclick=function(){
				var start = {
					min: laydate.now()
					,max: '2099-06-16 23:59:59'
					,istoday: false
					,choose: function(datas){
						var value=datas;
						that.addProperty('EndTime',value)
					},
					istime: true,
					format: 'YYYY-MM-DD hh:mm:ss'
				};
				start.elem = this;
				laydate(start);
			};
			applyDefalutValue(graph,textarea61,"EndTime");
			div.appendChild(span61);
			div.appendChild(textarea61);

			var list=[];
			$.ajax({
				type: "POST",
				dataType: "json",
				async:false,
				url: server_url + "/companyUser/list",
				data: {
					"token": "futurever"
				},
				success: function(res) {
					var companyUser = res.data;
					var jsons={};
					for(var i = 0; i < companyUser.length; i++) {
						jsons={
							"text":companyUser[i].name,
							"name":companyUser[i].pubkey
						}
						list.push(jsons);

					}
				},
				error: function(err) {
					console.log(err);
				}
			});











			var span71 = createSpan("合约版本");
			var textarea71=createTextarea(function(evt) {
				var value =textarea71.value;
				that.addProperty("MetaAttributeVersion",value);
			});
			applyDefalutValue(graph,textarea71,"MetaAttributeVersion");
			div.appendChild(span71);
			div.appendChild(textarea71);

			var span72 = createSpan("合约版权");
			var textarea72=createTextarea(function(evt)
			{
				var value =textarea72.value;
				that.addProperty("MetaAttributeCopyright",value);
			});
			applyDefalutValue(graph,textarea72,"MetaAttributeCopyright");
			div.appendChild(span72);
			div.appendChild(textarea72);

            // ----------------------合约主体Start-----------------------------
			var divC=document.createElement('div');
			divC.style.borderTop='1px dashed #ccc';
            divC.style.marginTop='10px';
            var span70 = createSpan("合约主体")
            span70.style.fontWeight='bold';
            span70.style.marginTop='5px';
            // 加号
            var sp=document.createElement('span');
            sp.style.width='20px';
            sp.style.height='20px';
            sp.style.fontSize='20px';
            sp.style.marginLeft='164px';
            sp.innerHTML='+';
            sp.style.cursor='pointer';
            divC.appendChild(span70);
            divC.appendChild(sp);
            div.appendChild(divC);
            var sum1 = 1;
            var sum1=returnValue(graph,null,"sum1")?returnValue(graph,null,"sum1"):1;
            sp.onclick=function(){
                sum1++;
                that.addProperty("sum1",sum1);
                var divCon=document.createElement('div');
                var textarea70=createOptions(list,function(evt)
                {
                    var value=textarea70.selectedOptions[0].value;
                    that.addProperty("ContractOwners"+sum1,value);
                });
                textarea70.style.width='160px';
                textarea70.value='';
                var span701 = createSpan("owner"+sum1)
                span701.style.fontWeight='normal';
                span701.style.background='none';
                span701.style.border='none';
                var span31=document.createElement('span');
                span31.innerHTML='-';
                span31.style.display='inline-block';
                span31.style.width='20px';
                span31.style.fontSize='20px';
                span31.style.textAlign='center';
                span31.style.cursor='pointer';
                span31.setAttribute('sum1',sum1)
                span31.onclick=function(){
                    div.removeChild(divCon);
                    var sum1=returnValue (graph,null,'sum1');
                    var signs=span31.getAttribute('sum1');
                    that.removeProperty('ContractOwners'+(signs-1));
                    // dom.addProperty("sum",sum-1);
                }
                divCon.appendChild(span701);
                divCon.appendChild(textarea70);
                divCon.appendChild(span31);
                div.insertBefore(divCon,span8)
            }
            for(var i=1;i<=sum1;i++){
                (function (j) {

                    var v = returnValue(graph,null,"ContractOwners"+j)
                    if(v || (j==1&&sum1==1)){
                        var divCon=document.createElement('div');
                        var textarea70=createOptions(list,function(evt)
                        {
                            var value=textarea70.selectedOptions[0].value;
                            that.addProperty("ContractOwners"+j,value);
                        });
                        for (var x=0;x<list.length;x++){
                            if(v=list[x].pubkey){
                                textarea70.value=item[x].text;
                            }
                        }
                        applyDefalutValue(graph,textarea70,"ContractOwners"+j);
                        textarea70.style.width='160px';
                        var span701 = createSpan("owner"+j)
                        span701.style.fontWeight='normal';
                        span701.style.background='none';
                        span701.style.border='none';
                        var span31=document.createElement('span');
                        span31.innerHTML='-';
                        span31.style.display='inline-block';
                        span31.style.width='20px';
                        span31.style.fontSize='20px';
                        span31.style.textAlign='center';
                        span31.style.cursor='pointer';
                        span31.setAttribute('sum1',sum1)
                        span31.onclick=function(){
                            div.removeChild(divCon);
                            var sumAttribute=returnValue (graph,null,'sum1');
                            var signs=span3.getAttribute('sum1');
                            that.removeProperty('ContractOwners'+j);
                            // dom.addProperty("sum",sum-1);
                        }
                        divCon.appendChild(span701);
                        divCon.appendChild(textarea70);
                        divCon.appendChild(span31);
                        div.insertBefore(divCon,span8)
                    }
                })(i);
            }

            // ----------------------合约主体End------------------------------------------
			var span8 = createTitle("合约资产");
			div.appendChild(span8);

            var span10 = createSpan("标识");
            var textarea10=createTextarea(function(evt)
            {
                var value =textarea10.value;
                that.addProperty("ContractAssets_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'ContractAssets_Cname',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea10,vali)
                }
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea10,"ContractAssets_Cname");
            var divLine=document.createElement('div');
            div.appendChild(span10);
            div.appendChild(textarea10);

			var span9 = createSpan("描述");
			var textarea9=createText(function(evt)
			{
				var value =textarea9.value;
				that.addProperty("ContractAssets_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'ContractAssets_Description',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea9,vali)
                }
			});
			applyDefalutValue(graph,textarea9,"ContractAssets_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span9.style.float='left';
			span9.style.height='38px';
			span9.style.lineHeight='38px';
			span9.style.marginTop='2px';
			divLine.appendChild(span9);
			divLine.appendChild(textarea9);
			div.appendChild(divLine);

			var span12 = createSpan("数量");
			var textarea12=createTextarea(function(evt)
			{
				var value =textarea12.value;
				that.addProperty("ContractAssets_Amount",value);
                var vali = validateCommonCell(cell.getTypeId(),'ContractAssets_Amount',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea12,vali)
                }
            });
            textarea12.style.marginTop='20px';
			applyDefalutValue(graph,textarea12,"ContractAssets_Amount");
			div.appendChild(span12);
			div.appendChild(textarea12);

			var span11 = createSpan("单位");
			var textarea11=createTextarea(function(evt)
			{
				var value =textarea11.value;
				that.addProperty("ContractAssets_Unit",value);
			});
			applyDefalutValue(graph,textarea11,"ContractAssets_Unit");
			div.appendChild(span11);
			div.appendChild(textarea11);

			// var span13 = createSpan("产品代码")
			//    var textarea13=createTextarea(function(evt)
			//    {
			//        var value =textarea13.value;
			//        that.addProperty("AttributeProductNum",value);
			//    });
			//    applyDefalutValue(graph,textarea13,"AttributeProductNum");
			//    div.appendChild(span13);
			//    div.appendChild(textarea13);

			// var span14 = createSpan("发行规模")
			//    var textarea14=createTextarea(function(evt)
			//    {
			//        var value =textarea14.value;
			//        that.addProperty("AttributeProductSize",value);
			//    });
			//    applyDefalutValue(graph,textarea14,"AttributeProductSize");
			//    div.appendChild(span14);
			//    div.appendChild(textarea14);

			// var span15 = createSpan("募集期")
			//    var textarea15=createTextarea(function(evt)
			//    {
			//        var value =textarea15.value;
			//        that.addProperty("AttributeCollectTime",value);
			//    });
			//    applyDefalutValue(graph,textarea15,"AttributeCollectTime");
			//    div.appendChild(span15);
			//    div.appendChild(textarea15);

			// var span16 = createSpan("风险提醒")
			//    var textarea16=createTextarea(function(evt)
			//    {
			//        var value =textarea16.value;
			//        that.addProperty("AttributeVenture",value);
			//    });
			//    applyDefalutValue(graph,textarea16,"AttributeVenture");
			//    div.appendChild(span16);
			//    div.appendChild(textarea16);

			// var span16 = createSpan("购买须知")
			//    var textarea16=createTextarea(function(evt)
			//    {
			//        var value =textarea16.value;
			//        that.addProperty("AttributeNotice",value);
			//    });
			//    applyDefalutValue(graph,textarea16,"AttributeNotice");
			//    div.appendChild(span16);
			//    div.appendChild(textarea16);
			// -------------------------合约资产自定义属性Statr---------------------

            var span032=createSpan('添加当前资产的其他属性描述');
            span032.style.width='234px';
            span032.style.fontWeight='normal';
            var sp2=document.createElement('span');
            sp2.style.width='20px';
            sp2.style.height='20px';
            sp2.style.fontSize='20px';
            sp2.innerHTML='+';
            sp2.style.cursor='pointer';
            var sumAssets = 1;
            div.appendChild(span032);
            div.appendChild(sp2);
            var sumAssets=returnValue(graph,null,"sumAssets")?returnValue(graph,null,"sumAssets"):1;
            sp2.onclick=function(){
                sumAssets++;
                that.addProperty("sumAssets",sumAssets);
                var range=createAssets(
                    function(evt){
                        var oInput=range.getElementsByTagName('input')[0]
                        that.addProperty("ContractAssetsKey"+sumAssets,oInput.value);
                    },
                    function(evt){
                        var oInput2=range.getElementsByTagName('input')[1]
                        that.addProperty("ContractAssetsValue"+sumAssets,oInput2.value);
                    },that,sumAssets
                );
                div.insertBefore(range,line)
            }
            for(var i=1;i<=sumAssets;i++){
                (function (j) {
                    var v = returnValue(graph,null,"ContractAssetsKey"+j)
                    if(v || (j==1&&sumAssets==1)){
                        var range=createAssets(
                            function(evt){
                                var oInput=range.getElementsByTagName('input')[0]
                                that.addProperty("ContractAssetsKey"+j,oInput.value);
                            },
                            function(evt){
                                var oInput2=range.getElementsByTagName('input')[1]
                                that.addProperty("ContractAssetsValue"+j,oInput2.value);
                            },that,j
                        );
                        var oI2=range.getElementsByTagName('input');
                        applyDefalutValue(graph,oI2[0],"ContractAssetsKey"+j);
                        applyDefalutValue(graph,oI2[1],"ContractAssetsValue"+j);
                        div.insertBefore(range,line)
                    }
                })(i);
            }
			// ---------------------------合约资产自定义属性End------------------------
			// -----------------------自定义属性Start--------------------------
			var line=document.createElement('div');
			line.style.width='100%';
			line.style.borderTop='1px solid #c0c0c0';
            line.style.height='1px';
            line.style.marginTop='10px';
			div.appendChild(line);
            var span = document.createElement('div');
            span.style.width = '70px';
            span.style.marginTop = '15px';
            span.style.fontWeight = 'bold';
            span.style.fontSize = '14px';
            mxUtils.write(span, "自定义属性");
            div.appendChild(span);

            var line0=document.createElement('div');
            line0.style.width='100%';
            line0.style.borderTop='1px solid #c0c0c0';
            line0.style.height='1px';
            line0.style.marginTop='10px';
            div.appendChild(line0);

            var span031=createSpan('名称');
            var span0311=createSpan('属性值');
            span031.style.width='80px';
            span031.style.textAlign='center';
            span031.style.marginRight='10px';
            span031.style.fontWeight='bold';
            span0311.style.width='160px';
            span0311.style.textAlign='center';
            span0311.style.marginRight='0px';
            span0311.style.fontWeight='bold';
            var sp1=document.createElement('span');
            sp1.style.width='20px';
            sp1.style.height='20px';
            sp1.style.fontSize='20px';
            sp1.innerHTML='+';
            sp1.style.cursor='pointer';
            var sumAttribute = 1;
            div.appendChild(span031);
            div.appendChild(span0311);
            div.appendChild(sp1);
            var sumAttribute=returnValue(graph,null,"sumAttribute")?returnValue(graph,null,"sumAttribute"):1;
            sp1.onclick=function(){
                sumAttribute++;
                that.addProperty("sumAttribute",sumAttribute);
                var range=createAddArrange(
                    function(evt){
                        var oInput=range.getElementsByTagName('input')[0]
                        that.addProperty("MetaAttributeKey"+sumAttribute,oInput.value);
                    },
                    function(evt){
                        var oInput2=range.getElementsByTagName('input')[1]
                        that.addProperty("MetaAttributeValue"+sumAttribute,oInput2.value);
                    },that,sumAttribute
                );
                div.appendChild(range)
            }
            for(var i=1;i<=sumAttribute;i++){
                (function (j) {

                    var v = returnValue(graph,null,"MetaAttributeKey"+j)
                    if(v || (j==1&&sumAttribute==1)){
                        var range1=createAddArrange(
                            function(evt){
                                var oInput=range1.getElementsByTagName('input')[0]
                                that.addProperty("MetaAttributeKey"+j,oInput.value);
                            },
                            function(evt){
                                var oInput2=range1.getElementsByTagName('input')[1]
                                that.addProperty("MetaAttributeValue"+j,oInput2.value);
                            },that,j
                        );
                        var oI2=range1.getElementsByTagName('input');
                        applyDefalutValue(graph,oI2[0],"MetaAttributeKey"+j);
                        applyDefalutValue(graph,oI2[1],"MetaAttributeValue"+j);
                        div.appendChild(range1)
                    }
                })(i);
            }


            // -----------------------自定义属性End--------------------------
			break;
		case '1'://决策
			debugger
            var span3 = createSpan("标识")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span3.appendChild(xing);
            var textarea3=createTextarea(function(evt)
            {
                var value =textarea3.value;
                that.addProperty("Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'Cname',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea3,vali)
                }
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea3,"Cname");
            div.appendChild(span3);
            div.appendChild(textarea3);

			var span1 = createSpan("名称")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span1.appendChild(xing);
			var textarea1=createTextarea(function(evt)
			{
				var value =textarea1.value;

				that.addProperty("Caption",value);
				that.addProperty("label",value);
                var vali = validateCommonCell(cell.getTypeId(),'Caption',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea1,vali)
                }
			});
			applyDefalutValue(graph,textarea1,"Caption");
			div.appendChild(span1);
			div.appendChild(textarea1);
			
			var span2 = createSpan("描述")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span2.appendChild(xing);
			var textarea2=createText(function(evt)
			{
				var value =textarea2.value;
				that.addProperty("Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'Description',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea2,vali)
                }
			});
			
			applyDefalutValue(graph,textarea2,"Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span2.style.float='left';
			span2.style.height='38px';
			span2.style.lineHeight='38px';
			span2.style.marginTop='2px';
			divLine.appendChild(span2);
			divLine.appendChild(textarea2);
			div.appendChild(divLine);

			var span8 = createTitle("前置条件");
			span8.style.marginTop='20px';
			div.appendChild(span8);

            var span10 = createSpan("标识")
            var textarea10=createTextarea(function(evt)
            {
                var value =textarea10.value;
                that.addProperty("PreCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_Cname',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea10,vali)
                }
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea10,"PreCondition_Cname");
            div.appendChild(span10);
            div.appendChild(textarea10);

			var span9 = createSpan("描述")
			var textarea9=createText(function(evt)
			{
				var value =textarea9.value;
				that.addProperty("PreCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea9,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea9,"PreCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span9.style.float='left';
			span9.style.height='38px';
			span9.style.lineHeight='38px';
			span9.style.marginTop='2px';
			divLine.appendChild(span9);
			divLine.appendChild(textarea9);
			div.appendChild(divLine);

			var span11 = createSpan("条件")
			span11.style.marginTop='20px';
			var textarea11=createTextarea(function(evt)
			{
				var value =textarea11.value;
				that.addProperty("PreCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea11,vali)
                }
                console.info(vali);

            });
			applyDefalutValue(graph,textarea11,"PreCondition_ExpressionStr");
			div.appendChild(span11);
			div.appendChild(textarea11);

			var span12 = createTitle("候选条件");
			div.appendChild(span12);


            var span14 = createSpan("标识")
            span14.style.marginTop='20px';
            var textarea14=createTextarea(function(evt)
            {
                var value =textarea14.value;
                that.addProperty("CandidateList_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'CandidateList_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea14,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea14,"CandidateList_Cname");
            div.appendChild(span14);
            div.appendChild(textarea14);

 			var span13 = createSpan("描述")
            span13.style.marginTop='20px';
			var textarea13=createText(function(evt)
			{
				var value =textarea13.value;
				that.addProperty("CandidateList_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'CandidateList_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea13,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea13,"CandidateList_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span13.style.float='left';
			span13.style.height='38px';
			span13.style.lineHeight='38px';
			span13.style.marginTop='2px';
			divLine.appendChild(span13);
			divLine.appendChild(textarea13);
			div.appendChild(divLine);

            // var span15 = createSpan("支持")
            // var textarea15=createTextarea(function(evt)
            // {
				// var value =textarea15.value;
				// that.addProperty("CandidateList_Arguments",value);
            // });
            // applyDefalutValue(graph,textarea15,"CandidateList_Arguments");
            // div.appendChild(span15);
            // div.appendChild(textarea15);


            // ----------------------支持Start-----------------------------
			var divZ=document.createElement('div');
            divZ.style.marginTop='20px';
            divZ.style.borderTop='1px dashed #ccc';
            divZ.style.marginTop='10px';
            var span15 = createSpan("支持")
			span15.style.fontWeight='bold';
            // 加号
            var sp15=document.createElement('span');
            sp15.style.width='20px';
            sp15.style.height='20px';
            sp15.style.fontSize='20px';
            sp15.style.marginLeft='164px';
            sp15.innerHTML='+';
            sp15.style.cursor='pointer';
            divZ.appendChild(span15);
            divZ.appendChild(sp15);
            div.appendChild(divZ);
            var sumArguments = 1;
            var sumArguments=returnValue(graph,null,"sumArguments")?returnValue(graph,null,"sumArguments"):1;
            sp15.onclick=function(){
                sumArguments++;
                that.addProperty("sumArguments",sumArguments);
                var divCon=document.createElement('div');
                var textarea15=createTextarea(function(evt)
                {
                    var value =textarea15.value;
                    that.addProperty("CandidateList_Arguments"+sumArguments,value);
                });
                textarea15.style.width='160px';
                var span151 = createSpan("支持条件"+sumArguments)
                span151.style.fontWeight='normal';
                span151.style.background='none';
                span151.style.border='none';
                var span31=document.createElement('span');
                span31.innerHTML='-';
                span31.style.display='inline-block';
                span31.style.width='20px';
                span31.style.fontSize='20px';
                span31.style.textAlign='center';
                span31.style.cursor='pointer';
                span31.setAttribute('sumArguments',sumArguments)
                divCon.appendChild(span151);
                divCon.appendChild(textarea15);
                divCon.appendChild(span31);
                span31.onclick=function(){
                    div.removeChild(divCon);
                    var sumArguments=returnValue (graph,null,'sumArguments');
                    var signs=span31.getAttribute('sumArguments');
                    console.log(signs)
                    that.removeProperty('ContractOwners'+(signs-1));
                }
                div.insertBefore(divCon,divA)
            }
            for(var i=1;i<=sumArguments;i++){
                (function (j) {

                    var v = returnValue(graph,null,"CandidateList_Arguments"+j)
                    if(v || (j==1&&sumArguments==1)){
                        var divCon=document.createElement('div');
                        var textarea15=createTextarea(function(evt)
                        {
                            var value =textarea15.value;
                            that.addProperty("CandidateList_Arguments"+j,value);
                        });
                        applyDefalutValue(graph,textarea15,"CandidateList_Arguments"+j);
                        textarea15.style.width='160px';
                        var span151 = createSpan("支持条件"+j)
                        span151.style.fontWeight='normal';
                        span151.style.background='none';
                        span151.style.border='none';
                        var span31=document.createElement('span');
                        span31.innerHTML='-';
                        span31.style.display='inline-block';
                        span31.style.width='20px';
                        span31.style.fontSize='20px';
                        span31.style.textAlign='center';
                        span31.style.cursor='pointer';
                        span31.setAttribute('sumArguments',sumArguments)
                        divCon.appendChild(span151);
                        divCon.appendChild(textarea15);
                        divCon.appendChild(span31);
                        span31.onclick=function(){
                            div.removeChild(divCon);
                            var sumArguments=returnValue (graph,null,'sumArguments');
                            var signs=span3.getAttribute('sumArguments');
                            console.log(signs)
                            that.removeProperty('CandidateList_Arguments'+j);
                        }
                        div.insertBefore(divCon,divA)
                    }
                })(i);
            }

            // ----------------------支持End------------------------------------------

            // ----------------------反对Start-----------------------------
			var divA=document.createElement('div');
			divA.style.borderTop='1px dashed #ccc';
			divA.style.marginTop='10px';
            var span16 = createSpan("反对")
            span16.style.fontWeight='bold';
            // 加号
            var sp16=document.createElement('span');
            sp16.style.width='20px';
            sp16.style.height='20px';
            sp16.style.fontSize='20px';
            sp16.style.marginLeft='164px';
            sp16.innerHTML='+';
            sp16.style.cursor='pointer';
            divA.appendChild(span16);
            divA.appendChild(sp16);
            div.appendChild(divA);
            var sumAgains = 1;
            var sumAgains=returnValue(graph,null,"sumAgains")?returnValue(graph,null,"sumAgains"):1;
            sp16.onclick=function(){
                sumAgains++;
                that.addProperty("sumAgains",sumAgains);
                var divCon=document.createElement('div');
                var textarea16=createTextarea(function(evt)
                {
                    var value =textarea16.value;
                    that.addProperty("CandidateList_AgainstArguments"+sumAgains,value);
                });
                textarea16.style.width='160px';
                var span161 = createSpan("反对条件"+sumAgains)
				span161.style.fontWeight='normal';
                span161.style.background='none';
                span161.style.border='none';
                var span31=document.createElement('span');
                span31.innerHTML='-';
                span31.style.display='inline-block';
                span31.style.width='20px';
                span31.style.fontSize='20px';
                span31.style.textAlign='center';
                span31.style.cursor='pointer';
                span31.setAttribute('sumAgains',sumAgains)
                divCon.appendChild(span161);
                divCon.appendChild(textarea16);
                divCon.appendChild(span31);
                span31.onclick=function(){
                    div.removeChild(divCon);
                    var sumArguments=returnValue (graph,null,'sumArguments');
                    var signs=span31.getAttribute('sumArguments');
                    console.log(signs)
                    that.removeProperty('ContractOwners'+(signs-1));
                }
                div.insertBefore(divCon,span17)
            }
            for(var i=1;i<=sumAgains;i++){
                (function (j) {

                    var v = returnValue(graph,null,"CandidateList_Arguments"+j)
                    if(v || (j==1&&sumAgains==1)){
                        var divCon=document.createElement('div');
                        var textarea16=createTextarea(function(evt)
                        {
                            var value =textarea16.value;
                            that.addProperty("CandidateList_AgainstArguments"+j,value);
                        });
                        applyDefalutValue(graph,textarea16,"CandidateList_AgainstArguments"+j);
                        textarea16.style.width='160px';
                        var span161 = createSpan("反对条件"+j)
                        span161.style.fontWeight='normal';
                        span161.style.background='none';
                        span161.style.border='none';
                        var span31=document.createElement('span');
                        span31.innerHTML='-';
                        span31.style.display='inline-block';
                        span31.style.width='20px';
                        span31.style.fontSize='20px';
                        span31.style.textAlign='center';
                        span31.style.cursor='pointer';
                        span31.setAttribute('sumAgains',sumAgains)
                        divCon.appendChild(span161);
                        divCon.appendChild(textarea16);
                        divCon.appendChild(span31);
                        span31.onclick=function(){
                            div.removeChild(divCon);
                            var sumAgains=returnValue (graph,null,'sumAgains');
                            var signs=span3.getAttribute('sumAgains');
                            console.log(signs)
                            that.removeProperty('CandidateList_AgainstArguments'+j);
                        }
                        div.insertBefore(divCon,span17)
                    }
                })(i);
            }

            // ----------------------反对End------------------------------------------
			
			// var span16 = createSpan("反对")
			// var textarea16=createTextarea(function(evt)
			// {
			// 	var value =textarea16.value;
			// 	that.addProperty("CandidateList_AgainstArguments",value);
			// });
			// applyDefalutValue(graph,textarea16,"CandidateList_AgainstArguments");
			// var divd=document.createElement('div');
			// divd.appendChild(span16)
            // divd.appendChild(textarea16);
			// div.appendChild(divd);
            //
			// var span25 = createLittleTitle("决策结果");
            // var divd=document.createElement('div');
            // divd.appendChild(span25)
            // div.appendChild(divd);
            //
            // var span27 = createSpan("标识")
            // var textarea27=createTextarea(function(evt)
            // {
             //    var value =textarea27.value;
             //    that.addProperty("DecisionResult_Cname",value);
            // });
            // applyDefalutValue(graph,textarea27,"DecisionResult_Cname");
            // div.appendChild(span27);
            // div.appendChild(textarea27);
            //
			// var span26 = createSpan("描述")
			// var textarea26=createText(function(evt)
			// {
			// 	var value =textarea26.value;
			// 	that.addProperty("DecisionResult_Description",value);
			// });
			// applyDefalutValue(graph,textarea26,"DecisionResult_Description");
			// var divLine=document.createElement('div');
            // divLine.style.height='40px';
			// span26.style.float='left';
			// span26.style.height='38px';
			// span26.style.lineHeight='38px';
			// span26.style.marginTop='2px';
			// divLine.appendChild(span26);
			// divLine.appendChild(textarea26);
			// div.appendChild(divLine);

			var span17 = createTitle("完成条件");
			div.appendChild(span17);
            var span19 = createSpan("标识")
            var textarea19=createTextarea(function(evt)
            {
                var value =textarea19.value;
                that.addProperty("CompleteCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea19,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea19,"CompleteCondition_Cname");
            var divLine=document.createElement('div');
            div.appendChild(span19);
            div.appendChild(textarea19);

			var span18 = createSpan("描述")
			var textarea18=createText(function(evt)
			{
				var value =textarea18.value;
				that.addProperty("CompleteCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea18,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea18,"CompleteCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span18.style.float='left';
			span18.style.height='38px';
			span18.style.lineHeight='38px';
			span18.style.marginTop='2px';
			divLine.appendChild(span18);
			divLine.appendChild(textarea18);
			div.appendChild(divLine);

			var span20 = createSpan("条件")
            span20.style.marginTop='20px';
			var textarea20=createTextarea(function(evt)
			{
				var value =textarea20.value;
				that.addProperty("CompleteCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea20,vali)
                }
                console.info(vali);
            });
			applyDefalutValue(graph,textarea20,"CompleteCondition_ExpressionStr");
			div.appendChild(span20);
			div.appendChild(textarea20);

			var span21 = createTitle("终止条件");
			div.appendChild(span21);

            var span23 = createSpan("标识")
            var textarea23=createTextarea(function(evt)
            {
                var value =textarea23.value;
                that.addProperty("DiscardCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea23,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea23,"DiscardCondition_Cname");
            div.appendChild(span23);
            div.appendChild(textarea23);

			var span22 = createSpan("描述")
            span22.style.marginTop='20px';
			var textarea22=createText(function(evt)
			{
				var value =textarea22.value;
				that.addProperty("DiscardCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea22,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea22,"DiscardCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span22.style.float='left';
			span22.style.height='38px';
			span22.style.lineHeight='38px';
			span22.style.marginTop='2px';
			divLine.appendChild(span22);
			divLine.appendChild(textarea22);
			div.appendChild(divLine);

			var span24 = createSpan("条件")
			var textarea24=createTextarea(function(evt)
			{
				var value =textarea24.value;
				that.addProperty("DiscardCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea24,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea24,"DiscardCondition_ExpressionStr");
			div.appendChild(span24);
			div.appendChild(textarea24);

			break;
		case '2'://查询
            var span3 = createSpan("标识")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span3.appendChild(xing);
            var textarea3=createTextarea(function(evt)
            {
                var value =textarea3.value;
                that.addProperty("Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'Cname',value,graph.getCnameArray());
                console.info(vali);
                if(vali){
                    titleMsg(textarea3,vali)
                }
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea3,"Cname");
            var divLine=document.createElement('div');
            divLine.appendChild(span3);
            divLine.appendChild(textarea3);
            div.appendChild(divLine);

			var span1 = createSpan("名称")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span1.appendChild(xing);
			var textarea1=createTextarea(function(evt)
			{
				var value =textarea1.value;
				that.addProperty("Caption",value);
				that.addProperty("label",value);
                var vali = validateCommonCell(cell.getTypeId(),'Caption',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea2,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea1,"Caption");
			div.appendChild(span1);
			div.appendChild(textarea1);

			var span2 = createSpan("描述")

            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span2.appendChild(xing);
			var textarea2=createText(function(evt)
			{
				var value =textarea2.value;
				that.addProperty("Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea2,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea2,"Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span2.style.float='left';
			span2.style.height='38px';
			span2.style.lineHeight='38px';
			span2.style.marginTop='2px';
			divLine.appendChild(span2);
			divLine.appendChild(textarea2);
			div.appendChild(divLine);

			var span8 = createTitle("前置条件");
            span8.style.marginTop='20px';
			div.appendChild(span8);


            var span10 = createSpan("标识")
            var textarea10=createTextarea(function(evt)
            {
                var value =textarea10.value;
                that.addProperty("PreCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea10,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea10,"PreCondition_Cname");
            div.appendChild(span10);
            div.appendChild(textarea10);

			var span9 = createSpan("描述")
			var textarea9=createText(function(evt)
			{
				var value =textarea9.value;
				that.addProperty("PreCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea9,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea9,"PreCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span9.style.float='left';
			span9.style.height='38px';
			span9.style.lineHeight='38px';
			span9.style.marginTop='2px';
			divLine.appendChild(span9);
			divLine.appendChild(textarea9);
			div.appendChild(divLine);

			var span11 = createSpan("条件")
            span11.style.marginTop='20px';
			var textarea11=createTextarea(function(evt)
			{
				var value =textarea11.value;
				that.addProperty("PreCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea11,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea11,"PreCondition_ExpressionStr");
			div.appendChild(span11);
			div.appendChild(textarea11);

			var span12 = createTitle("查询方法");
			div.appendChild(span12);

            var span14 = createSpan("标识")
            span14.style.marginTop='20px';
            var textarea14=createTextarea(function(evt)
            {
                var value =textarea14.value;
                that.addProperty("DataValueSetterExpressionList_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'DataValueSetterExpressionList_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea14,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);

            });
            applyDefalutValue(graph,textarea14,"DataValueSetterExpressionList_Cname");
            div.appendChild(span14);
            div.appendChild(textarea14);


            var span13 = createSpan("描述")
            span13.style.marginTop='20px';
            var textarea13=createText(function(evt)
            {
                var value =textarea13.value;
                that.addProperty("DataValueSetterExpressionList_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'DataValueSetterExpressionList_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea13,vali)
                }
                console.info(vali);
            });
            applyDefalutValue(graph,textarea13,"DataValueSetterExpressionList_Description");
            var divLine=document.createElement('div');
            divLine.style.height='40px';
            span13.style.float='left';
            span13.style.height='38px';
            span13.style.lineHeight='38px';
            span13.style.marginTop='2px';
            divLine.appendChild(span13);
            divLine.appendChild(textarea13);
            div.appendChild(divLine);

			var span02 = createSpan("动作")
            span02.style.marginTop='20px';
			var textarea02=createOptions(functionData.demo13,function (evt) {
                $('#div022').empty();
                var value=textarea02.selectedOptions[0].value;
                that.addProperty("DataValueSetterExpressionList_ExpressionStr",value);
                console.log(value)
                var valueArr=value.split('(')[1].split(')')[0].split(',');
                var str=value.split('(')[0]
                console.log(valueArr[0]);
                if(valueArr.length){
                    $('#bigdiv').empty();
                    if(valueArr[0]!=''){
                        for(var i=0;i<valueArr.length;i++){
                            (function (j,str) {
                                var textarea021=createTextarea(function(evt)
                                {
                                    var value =textarea021.value;
                                    valueArr[j]=value;
                                    var str1=valueArr.join(',');
                                    var str2=str+'('+str1+')';
                                    that.addProperty("DataValueSetterExpressionList_ExpressionStr",str2);
                                });
                                console.log(valueArr[j])
                                console.log(textarea021)
                                textarea021.value=valueArr[j];
                                textarea021.style.width='180px';
                                var span021 = createSpan("参数"+j+':')
                                span021.style.fontWeight='normal';
                                span021.style.background='none';
                                span021.style.border='none';
                                var div02=document.createElement('div');
                                div02.appendChild(span021);
                                div02.appendChild(textarea021);
                                bigdiv.appendChild(div02)
                            })(i,str);
                        }
					}
                }
            });
            var v=returnValue(graph,null,'DataValueSetterExpressionList_ExpressionStr');
            div.appendChild(span02);
            div.appendChild(textarea02);
            var div022=document.createElement('div');
            div022.setAttribute('id','div022')
            div.appendChild(div022)
			if(v){
                var attrStr='';
                var arrV=v.split(/[(),]/);
                if(arrV.length==3){
                    if(arrV[1]==''){
                        attrStr=v;
                    }else{
                        attrStr=arrV[0]+'(a1)';
                    }
                }else{
                    var h=arrV.length-3;
                    for(var i=0;i<h;i++){
                        attrStr+='a'+(i+1)+',';
                    }
                    attrStr+='a'+(h+1);
                    attrStr=arrV[0]+'('+attrStr+')';
                }
                console.log(attrStr)
                textarea02.value = attrStr;
                var value=v;
                console.log(value)
                var valueArr=value.split('(')[1].split(')')[0].split(',');
                var str=value.split('(')[0]
                console.log(valueArr[0]);
                if(valueArr[0]){
                    for(var i=0;i<valueArr.length;i++){
						(function (j,str) {
                            var textarea021=createTextarea(function(evt)
                            {
                                var value =textarea021.value;
                                valueArr[j]=value;
                                var str1=valueArr.join(',');
                                var str2=str+'('+str1+')';
                                that.addProperty("DataValueSetterExpressionList_ExpressionStr",str2);
                            });
                            textarea021.value=valueArr[j];
                            textarea021.style.width='180px';
                            var span021 = createSpan("参数"+j+':')
                            span021.style.fontWeight='normal';
                            span021.style.background='none';
                            span021.style.border='none';
                            var div02=document.createElement('div');
                            div02.appendChild(span021);
                            div02.appendChild(textarea021);
                            div022.appendChild(div02);
                        })(i,str);
                    }
                }
			}else{
                textarea02.value='';
			}

			var bigdiv=document.createElement('div');
			bigdiv.setAttribute('id','bigdiv');
			div.appendChild(bigdiv);

			var span17 = createTitle("查询结果");
            var div17=document.createElement('div');
            div17.appendChild(span17);
            div.appendChild(div17);


            var span19 = createSpan("标识")
            span19.style.marginTop='20px';
            var textarea19=createTextarea(function(evt)
            {
                var value =textarea19.value;
                that.addProperty("DataList_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'DataList_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea19,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea19,"DataList_Cname");
            div.appendChild(span19);
            div.appendChild(textarea19);

			var span18 = createSpan("描述")
            span18.style.marginTop='20px';
			var textarea18=createText(function(evt)
			{
				var value =textarea18.value;
				that.addProperty("DataList_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'DataList_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea18,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea18,"DataList_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span18.style.float='left';
			span18.style.height='38px';
			span18.style.lineHeight='38px';
			span18.style.marginTop='2px';
			divLine.appendChild(span18);
			divLine.appendChild(textarea18);
			div.appendChild(divLine);


            var span25 = createSpan("类型")
            span25.style.marginTop='20px';
            var arry = [
                {name:'Data_Numeric_Uint',text:'无符号整型'},
                {name:'Data_Numeric_Int',text:'有符号整型'},
                {name:'Data_Numeric_Float',text:'浮点小数型'},
                {name:'Data_Text',text:'文本型'},
                {name:'Data_Bool ',text:'布尔型'},
                {name:'Data_Date ',text:'日期型'},
                {name:'Data_Array',text:'数组型'},
                {name:'Data_DecisionCandidate',text:'决策型'},
                {name:'Data_OperateResultData',text:'通用操作结果'}
            ];
            var textarea25=createOptions(arry,function(evt)
            {
                var value=textarea25.selectedOptions[0].value;
                // var text =textarea25.selectedOptions[0].text;
                that.addProperty("DataList_HardConvType",value);
                if(value=='Data_Numeric_Uint'||value=='Data_Numeric_Int'||value=='Data_Numeric_Float'){
                    range.style.display='block';
                }else{
                    range.style.display='none';
                }
            });
            applyDefalutValue(graph,textarea25,"DataList_HardConvType");
            div.appendChild(span25);
            div.appendChild(textarea25);
            var range=createRanges(
                function(evt){
                    var oInput=range.getElementsByTagName('input')[0]
                    console.log(oInput.value)
                    that.addProperty("DataList_DataRangeFrom",oInput.value);
                },
                function(evt){
                    var oInput2=range.getElementsByTagName('input')[1]
                    console.log(oInput2.value)
                    that.addProperty("DataList_DataRangeTo",oInput2.value);
                }
            );
            var oIn=range.getElementsByTagName('input');
            applyDefalutValue(graph,oIn[0],"DataList_DataRangeFrom");
            applyDefalutValue(graph,oIn[1],"DataList_DataRangeTo");
            var v=returnValue(graph,null,'DataList_HardConvType');
            if(v){

                if(v=='Data_Numeric_Uint'||v=='Data_Numeric_Int'||v=='Data_Numeric_Float'){
                    range.style.display='block';
                }else{
                    range.style.display='none';
                }
            }else{
                range.style.display='none';
            }
            div.insertBefore(range,span16);

			var span16 = createSpan("默认值")
			var textarea16=createTextarea(function(evt)
			{
				var value =textarea16.value;
				that.addProperty("DataList_DefaultValue",value);
			});
			applyDefalutValue(graph,textarea16,"DataList_DefaultValue");
			div.appendChild(span16);
			div.appendChild(textarea16);

			var span20 = createSpan("单位")
			var textarea20=createTextarea(function(evt)
			{
				var value =textarea20.value;
				that.addProperty("DataList_Unit",value);
			});
			applyDefalutValue(graph,textarea20,"DataList_Unit");
			div.appendChild(span20);
			div.appendChild(textarea20);

			var divF=document.createElement('div');
			divF.style.borderTop='1px dashed #ccc';
			divF.style.marginTop='10px';
			var span03=createSpan('选择分支');
			span03.style.fontWeight='bold';
			var sp=document.createElement('span');
			var sum=0;
			sp.style.width='20px';
			sp.style.height='20px';
			sp.style.fontSize='20px';
			sp.style.marginLeft='160px';
			sp.innerHTML='+';
			sp.style.cursor='pointer';
			var sum = 1;
            divF.appendChild(span03);
            divF.appendChild(sp);
            div.appendChild(divF);
            var span031=createSpan('分支');
            var span0311=createSpan('值');
            span031.style.width='110px';
            span031.style.textAlign='center';
            span031.style.marginRight='10px';
            span031.style.fontWeight='bold';
            span0311.style.width='115px';
            span0311.style.textAlign='center';
            span0311.style.marginRight='0px';
            span0311.style.fontWeight='bold';
            div.appendChild(span031);
            div.appendChild(span0311);
            var sum=returnValue(graph,null,"sum")?returnValue(graph,null,"sum"):1;
            sp.onclick=function(){
                sum++;
                that.addProperty("sum",sum);
                var range=createAdd(
                    function(evt){
                        var oInput=range.getElementsByTagName('input')[0]
                        that.addProperty("SelectBranchs_BranchExpressionStr"+sum,oInput.value);
                        // console.log(oInput.value)
                        console.log(sum)
                    },
                    function(evt){
                        var oInput2=range.getElementsByTagName('input')[1]
                        // console.log(oInput2.value)
                        that.addProperty("SelectBranchs_BranchExpressionValue"+sum,oInput2.value);
                        // console.log(sum);
                    },that,sum
                    );
                div.insertBefore(range,span21)
            }
			for(var i=1;i<=sum;i++){
				(function (j) {

					var v = returnValue(graph,null,"SelectBranchs_BranchExpressionStr"+j)
					if(v || (j==1&&sum==1)){
                        var range1=createAdd(
                            function(evt){
                                var oInput=range1.getElementsByTagName('input')[0]
                                that.addProperty("SelectBranchs_BranchExpressionStr"+j,oInput.value);
                            },
                            function(evt){
                                var oInput2=range1.getElementsByTagName('input')[1]
                                that.addProperty("SelectBranchs_BranchExpressionValue"+j,oInput2.value);
                            },that,j
                        );
                        var oI2=range1.getElementsByTagName('input');
                        applyDefalutValue(graph,oI2[0],"SelectBranchs_BranchExpressionStr"+j);
                        applyDefalutValue(graph,oI2[1],"SelectBranchs_BranchExpressionValue"+j);
                        div.insertBefore(range1,span21)
					}
                })(i);
			}

			var span21 = createTitle("完成条件");
			div.appendChild(span21);

            var span23 = createSpan("标识")
            var textarea23=createTextarea(function(evt)
            {
                var value =textarea23.value;
                that.addProperty("CompleteCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea23,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea23,"CompleteCondition_Cname");
            div.appendChild(span23);
            div.appendChild(textarea23);

			var span22 = createSpan("描述")
			var textarea22=createText(function(evt)
			{
				var value =textarea22.value;
				that.addProperty("CompleteCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea22,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea22,"CompleteCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span22.style.float='left';
			span22.style.height='38px';
			span22.style.lineHeight='38px';
			span22.style.marginTop='2px';
			divLine.appendChild(span22);
			divLine.appendChild(textarea22);
			div.appendChild(divLine);

			var span24 = createSpan("条件")
            span24.style.marginTop='20px';
			var textarea24=createTextarea(function(evt)
			{
				var value =textarea24.value;
				that.addProperty("CompleteCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea24,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea24,"CompleteCondition_ExpressionStr");
			div.appendChild(span24);
			div.appendChild(textarea24);

			var span26 = createTitle("终止条件");
			div.appendChild(span26);

            var span28 = createSpan("标识")
            var textarea28=createTextarea(function(evt)
            {
                var value =textarea28.value;
                that.addProperty("DiscardCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea28,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea28,"DiscardCondition_Cname");
            div.appendChild(span28);
            div.appendChild(textarea28);

			var span27 = createSpan("描述")
			var textarea27=createText(function(evt)
			{
				var value =textarea27.value;
				that.addProperty("DiscardCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea27,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea27,"DiscardCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span27.style.float='left';
			span27.style.height='38px';
			span27.style.lineHeight='38px';
			span27.style.marginTop='2px';
			divLine.appendChild(span27);
			divLine.appendChild(textarea27);
			div.appendChild(divLine);

			var span29 = createSpan("条件")
            span29.style.marginTop='20px';
			var textarea29=createTextarea(function(evt)
			{
				var value =textarea29.value;
				that.addProperty("DiscardCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea29,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea29,"DiscardCondition_ExpressionStr");
			div.appendChild(span29);
			div.appendChild(textarea29);

			break;
		case '3'://动作
            var span3 = createSpan("标识")

            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span3.appendChild(xing);
            var textarea3=createTextarea(function(evt)
            {
                var value =textarea3.value;
                that.addProperty("Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea3,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea3,"Cname");
            div.appendChild(span3);
            div.appendChild(textarea3);

			var span1 = createSpan("名称")

            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span1.appendChild(xing);
			var textarea1=createTextarea(function(evt)
			{
				var value =textarea1.value;
				that.addProperty("Caption",value);
				that.addProperty("label",value);
                var vali = validateCommonCell(cell.getTypeId(),'Caption',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea1,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea1,"Caption");
			div.appendChild(span1);
			div.appendChild(textarea1);

			var span2 = createSpan("描述")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span2.appendChild(xing);
			var textarea2=createText(function(evt)
			{
				var value =textarea2.value;
				that.addProperty("Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea2,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea2,"Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span2.style.float='left';
			span2.style.height='38px';
			span2.style.lineHeight='38px';
			span2.style.marginTop='2px';
			divLine.appendChild(span2);
			divLine.appendChild(textarea2);
			div.appendChild(divLine);

			var span8 = createTitle("前置条件");
			span8.style.marginTop='20px'
			div.appendChild(span8);

            var span10 = createSpan("标识")
            var textarea10=createTextarea(function(evt)
            {
                var value =textarea10.value;
                that.addProperty("PreCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea10,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea10,"PreCondition_Cname");
            div.appendChild(span10);
            div.appendChild(textarea10);

			var span9 = createSpan("描述")
			var textarea9=createText(function(evt)
			{
				var value =textarea9.value;
				that.addProperty("PreCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea9,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea9,"PreCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span9.style.float='left';
			span9.style.height='38px';
			span9.style.lineHeight='38px';
			span9.style.marginTop='2px';
			divLine.appendChild(span9);
			divLine.appendChild(textarea9);
			div.appendChild(divLine);

			var span11 = createSpan("条件")
			span11.style.marginTop='20px';
			var textarea11=createTextarea(function(evt)
			{
				var value =textarea11.value;
				that.addProperty("PreCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea11,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea11,"PreCondition_ExpressionStr");
			div.appendChild(span11);
			div.appendChild(textarea11);

			var span12 = createTitle("执行动作");
			div.appendChild(span12);


            var span14 = createSpan("标识")
            span14.style.marginTop='20px';
            var textarea14=createTextarea(function(evt)
            {
                var value =textarea14.value;
                that.addProperty("DataValueSetterExpressionList_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'DataValueSetterExpressionList_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea14,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea14,"DataValueSetterExpressionList_Cname");
            div.appendChild(span14);
            div.appendChild(textarea14);

			var span13 = createSpan("描述")
            span13.style.marginTop='20px';
			var textarea13=createText(function(evt)
			{
				var value =textarea13.value;
				that.addProperty("DataValueSetterExpressionList_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'DataValueSetterExpressionList_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea13,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea13,"DataValueSetterExpressionList_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span13.style.float='left';
			span13.style.height='38px';
			span13.style.lineHeight='38px';
			span13.style.marginTop='2px';
			divLine.appendChild(span13);
			divLine.appendChild(textarea13);
			div.appendChild(divLine);


            var span02 = createSpan("动作")
            span02.style.marginTop='20px';
            var textarea02=createOptions(functionData.demo13,function (evt) {
                $('#div022').empty();
                var value=textarea02.selectedOptions[0].value;
                that.addProperty("DataValueSetterExpressionList_ExpressionStr",value);
                console.log(value)
                var valueArr=value.split('(')[1].split(')')[0].split(',');
                var str=value.split('(')[0]
                console.log(valueArr[0]);
                if(valueArr.length){
                    $('#bigdiv').empty();
                    if(valueArr[0]!=''){
                        for(var i=0;i<valueArr.length;i++){
                            (function (j,str) {
                                var textarea021=createTextarea(function(evt)
                                {
                                    var value =textarea021.value;
                                    valueArr[j]=value;
                                    var str1=valueArr.join(',');
                                    var str2=str+'('+str1+')';
                                    that.addProperty("DataValueSetterExpressionList_ExpressionStr",str2);
                                });
                                console.log(valueArr[j])
                                console.log(textarea021)
                                textarea021.value=valueArr[j];
                                textarea021.style.width='180px';
                                var span021 = createSpan("参数"+j+':')
                                span021.style.fontWeight='normal';
                                span021.style.background='none';
                                span021.style.border='none';
                                var div02=document.createElement('div');
                                div02.appendChild(span021);
                                div02.appendChild(textarea021);
                                bigdiv.appendChild(div02)
                            })(i,str);
                        }
                    }
                }
            });
            applyDefalutValue(graph,textarea02,"DataValueSetterExpressionList_ExpressionStr");
            div.appendChild(span02);
            div.appendChild(textarea02);
            var div022=document.createElement('div');
            div022.setAttribute('id','div022')
            div.appendChild(div022)
            var v=returnValue(graph,null,'DataValueSetterExpressionList_ExpressionStr');
            if(v){
                var attrStr='';
                var arrV=v.split(/[(),]/);
                if(arrV.length==3){
                    if(arrV[1]==''){
                        attrStr=v;
                    }else{
                        attrStr=arrV[0]+'(a1)';
                    }
                }else{
                    var h=arrV.length-3;
                    for(var i=0;i<h;i++){
                        attrStr+='a'+(i+1)+',';
                    }
                    attrStr+='a'+(h+1);
                    attrStr=arrV[0]+'('+attrStr+')';
                }
                console.log(attrStr)
                textarea02.value = attrStr;
                var value=v;
                console.log(value)
                var valueArr=value.split('(')[1].split(')')[0].split(',');
                var str=value.split('(')[0]
                console.log(valueArr[0]);
                if(valueArr[0]){
                    // $('#bigdiv').empty();
                    for(var i=0;i<valueArr.length;i++){
                        (function (j,str) {
                            var textarea021=createTextarea(function(evt)
                            {
                                var value =textarea021.value;
                                valueArr[j]=value;
                                var str1=valueArr.join(',');
                                var str2=str+'('+str1+')';
                                that.addProperty("DataValueSetterExpressionList_ExpressionStr",str2);
                            });
                            console.log(valueArr[j])
                            console.log(textarea021)
                            textarea021.value=valueArr[j];
                            textarea021.style.width='180px';
                            var span021 = createSpan("参数"+j+':')
                            span021.style.fontWeight='normal';
                            span021.style.background='none';
                            span021.style.border='none';
                            var div02=document.createElement('div');
                            div02.appendChild(span021);
                            div02.appendChild(textarea021);
                            div022.appendChild(div02);
                        })(i,str);
                    }
                }
                // that.addProperty("DataValueSetterExpressionList_ExpressionStr",value);
            }else{
                textarea02.value='';
            }
            var bigdiv=document.createElement('div');
            bigdiv.setAttribute('id','bigdiv');
            div.appendChild(bigdiv);
			var span17 = createTitle("动作结果");
			div.appendChild(span17);


            var span19 = createSpan("标识")
            span19.style.marginTop='20px';
            var textarea19=createTextarea(function(evt)
            {
                var value =textarea19.value;
                that.addProperty("DataList_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'DataList_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea19,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea19,"DataList_Cname");
            div.appendChild(span19);
            div.appendChild(textarea19);

			var span18 = createSpan("描述")
            span18.style.marginTop='20px';
			var textarea18=createText(function(evt)
			{
				var value =textarea18.value;
				that.addProperty("DataList_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'DataList_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea18,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea18,"DataList_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span18.style.float='left';
			span18.style.height='38px';
			span18.style.lineHeight='38px';
			span18.style.marginTop='2px';
			divLine.appendChild(span18);
			divLine.appendChild(textarea18);
			div.appendChild(divLine);


            var span25 = createSpan("类型")
            span25.style.marginTop='20px';
            var arry = [
                {name:'Data_Numeric_Uint',text:'无符号整型'},
                {name:'Data_Numeric_Int',text:'有符号整型'},
                {name:'Data_Numeric_Float',text:'浮点小数型'},
                {name:'Data_Text',text:'文本型'},
                {name:'Data_Bool ',text:'布尔型'},
                {name:'Data_Date ',text:'日期型'},
                {name:'Data_Array',text:'数组型'},
                {name:'Data_DecisionCandidate',text:'决策型'},
                {name:'Data_OperateResultData',text:'通用操作结果'}
            ];
            var textarea25=createOptions(arry,function(evt)
            {
                var value=textarea25.selectedOptions[0].value;
                // var text =textarea25.selectedOptions[0].text;
                that.addProperty("DataList_HardConvType",value);
                console.log(range)
                console.log(value)
                if(value=='Data_Numeric_Uint'||value=='Data_Numeric_Int'||value=='Data_Numeric_Float'){
                    range.style.display='block';
                }else{
                    range.style.display='none';
                }
            });
            applyDefalutValue(graph,textarea25,"DataList_HardConvType");
            div.appendChild(span25);
            div.appendChild(textarea25);
            var range=createRanges(
                function(evt){
                    var oInput=range.getElementsByTagName('input')[0]
                    console.log(oInput.value)
                    that.addProperty("DataList_DataRangeFrom",oInput.value);
                },
                function(evt){
                    var oInput2=range.getElementsByTagName('input')[1]
                    console.log(oInput2.value)
                    that.addProperty("DataList_DataRangeTo",oInput2.value);
                }
            );
            var oIn=range.getElementsByTagName('input');
            applyDefalutValue(graph,oIn[0],"DataList_DataRangeFrom");
            applyDefalutValue(graph,oIn[1],"DataList_DataRangeTo");
            var v=returnValue(graph,null,'DataList_HardConvType');
            if(v){

                if(v=='Data_Numeric_Uint'||v=='Data_Numeric_Int'||v=='Data_Numeric_Float'){
                    range.style.display='block';
                }else{
                    range.style.display='none';
                }
            }else{
                range.style.display='none';
            }
            div.insertBefore(range,span16);

			var span16 = createSpan("默认值")
			var textarea16=createTextarea(function(evt)
			{
				var value =textarea16.value;
				that.addProperty("DataList_DefaultValue",value);
			});
			applyDefalutValue(graph,textarea16,"DataList_DefaultValue");
			div.appendChild(span16);
			div.appendChild(textarea16);

			var span20 = createSpan("单位")
			var textarea20=createTextarea(function(evt)
			{
				var value =textarea20.value;
				that.addProperty("DataList_Unit",value);
			});
			applyDefalutValue(graph,textarea20,"DataList_Unit");
			div.appendChild(span20);
			div.appendChild(textarea20);



			// var span25 = createSpan("类型");
			//    var arry = [
			//    	{name:'Data_Numeric_Uint',text:'无符号整型1'},
			//    	{name:'Data_Numeric_Int',text:'有符号整型'},
			//    	{name:'Data_Numeric_Float',text:'浮点小数型'},
			//    	{name:'Data_Text',text:'文本型'},
            //      {name:'Data_Bool ',text:'布尔型'},
			//    	{name:'Data_Date ',text:'日期型'},
			//    	{name:'Data_Array',text:'数组型'},
			//    	{name:'Data_DecisionCandidate',text:'决策型'},
			//    	{name:'Data_OperateResultData',text:'通用操作结果'}
			//    	];
			//    var textarea25=createOptions(arry,function(evt)
			//    {
			//    	var value=textarea25.selectedOptions[0].value;
			//        that.addProperty("DataList_HardConvType",value);
			//        if(value=='Data_Numeric_Uint'||value=='Data_Numeric_Int'||value=='Data_Numeric_Float'){
			// 		ranges.style.display='block';
			//        }else{
			//        	ranges.style.display='none';
			//        }
			//  	});
			//  	var value=returnValue(graph,textarea25,"DataList_HardConvType");
			//       applyDefalutValue(graph,textarea25,"DataList_HardConvType");
			//    div.appendChild(span25);
			//    div.appendChild(textarea25);

			// var ranges=createRanges(
			// 	function(evt){
			//         var oInput=ranges.getElementsByTagName('input')[0];
			//         that.addProperty("DataList_DataRangeFrom",oInput.value);
			// 	},
			// 	function(evt){
			//         var oInput2=ranges.getElementsByTagName('input')[1];
			//         that.addProperty("DataList_DataRangeTo",oInput2.value);
			// 	}
			// );
			// var oI=ranges.getElementsByTagName('input');
			//    applyDefalutValue(graph,oI[0],"DataList_DataRangeFrom");
			//    applyDefalutValue(graph,oI[1],"DataList_DataRangeTo");
			//       if(value=='Data_Numeric_Uint'||value=='Data_Numeric_Int'||value=='Data_Numeric_Float'){
			// 	ranges.style.display='block';
			//       }else{
			// 	ranges.style.display='none';
			//       }
			// div.insertBefore(ranges,span21);
			// // div.appendChild(ranges);

			var span21 = createTitle("完成条件");
			div.appendChild(span21);

            var span23 = createSpan("标识")
            var textarea23=createTextarea(function(evt)
            {
                var value =textarea23.value;
                that.addProperty("CompleteCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea23,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea23,"CompleteCondition_Cname");
            div.appendChild(span23);
            div.appendChild(textarea23);

			var span22 = createSpan("描述")
			var textarea22=createText(function(evt)
			{
				var value =textarea22.value;
				that.addProperty("CompleteCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea22,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea22,"CompleteCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span22.style.float='left';
			span22.style.height='38px';
			span22.style.lineHeight='38px';
			span22.style.marginTop='2px';
			divLine.appendChild(span22);
			divLine.appendChild(textarea22);
			div.appendChild(divLine);

			var span24 = createSpan("条件")
			span24.style.marginTop='20px';
			var textarea24=createTextarea(function(evt)
			{
				var value =textarea24.value;
				that.addProperty("CompleteCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea24,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea24,"CompleteCondition_ExpressionStr");
			div.appendChild(span24);
			div.appendChild(textarea24);

			var span26 = createTitle("终止条件");
			div.appendChild(span26);

            var span28 = createSpan("标识")
            var textarea28=createTextarea(function(evt)
            {
                var value =textarea28.value;
                that.addProperty("DiscardCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea28,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea28,"DiscardCondition_Cname");
            div.appendChild(span28);
            div.appendChild(textarea28);

			var span27 = createSpan("描述")
			var textarea27=createText(function(evt)
			{
				var value =textarea27.value;
				that.addProperty("DiscardCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea27,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea27,"DiscardCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span27.style.float='left';
			span27.style.height='38px';
			span27.style.lineHeight='38px';
			span27.style.marginTop='2px';
			divLine.appendChild(span27);
			divLine.appendChild(textarea27);
			div.appendChild(divLine);

			var span29 = createSpan("条件")
            span29.style.marginTop='20px';
			var textarea29=createTextarea(function(evt)
			{
				var value =textarea29.value;
				that.addProperty("DiscardCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea29,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea29,"DiscardCondition_ExpressionStr");
			div.appendChild(span29);
			div.appendChild(textarea29);

			break;
		case '4'://计划
            var span3 = createSpan("标识")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span3.appendChild(xing);
            var textarea3=createTextarea(function(evt)
            {
                var value =textarea3.value;
                that.addProperty("Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea3,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea3,"Cname");
            div.appendChild(span3);
            div.appendChild(textarea3);

			var span1 = createSpan("名称")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span1.appendChild(xing);
			var textarea1=createTextarea(function(evt)
			{
				var value =textarea1.value;
				that.addProperty("Caption",value);
				that.addProperty("label",value);
                var vali = validateCommonCell(cell.getTypeId(),'Caption',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea1,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea1,"Caption");
			div.appendChild(span1);
			div.appendChild(textarea1);

			var span2 = createSpan("描述")
            var xing=document.createElement('span');
            xing.innerHTML='*';
            xing.style.fontSize='14px';
            xing.style.marginLeft='5px';
            xing.style.color='red';
            span2.appendChild(xing);
			var textarea2=createText(function(evt)
			{
				var value =textarea2.value;
				that.addProperty("Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea2,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea2,"Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span2.style.float='left';
			span2.style.height='38px';
			span2.style.lineHeight='38px';
			span2.style.marginTop='2px';
			divLine.appendChild(span2);
			divLine.appendChild(textarea2);
			div.appendChild(divLine);

			var span8 = createSpan1("前置条件");
			div.appendChild(span8);

            var span10 = createSpan("标识")
            var textarea10=createTextarea(function(evt)
            {
                var value =textarea10.value;
                that.addProperty("PreCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea10,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea10,"PreCondition_Cname");
            div.appendChild(span10);
            div.appendChild(textarea10);

			var span9 = createSpan("描述")
			var textarea9=createText(function(evt)
			{
				var value =textarea9.value;
				that.addProperty("PreCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea9,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea9,"PreCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span9.style.float='left';
			span9.style.height='38px';
			span9.style.lineHeight='38px';
			span9.style.marginTop='2px';
			divLine.appendChild(span9);
			divLine.appendChild(textarea9);
			div.appendChild(divLine);

			var span11 = createSpan("条件")
			var textarea11=createTextarea(function(evt)
			{
				var value =textarea11.value;
				that.addProperty("PreCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'PreCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea11,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea11,"PreCondition_ExpressionStr");
			div.appendChild(span11);
			div.appendChild(textarea11);

			var span21 = createSpan1("完成条件");
			div.appendChild(span21);

            var span23 = createSpan("标识")
            var textarea23=createTextarea(function(evt)
            {
                var value =textarea23.value;
                that.addProperty("CompleteCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea23,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea23,"CompleteCondition_Cname");
            div.appendChild(span23);
            div.appendChild(textarea23);

			var span22 = createSpan("描述")
			var textarea22=createText(function(evt)
			{
				var value =textarea22.value;
				that.addProperty("CompleteCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea22,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea22,"CompleteCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span22.style.float='left';
			span22.style.height='38px';
			span22.style.lineHeight='38px';
			span22.style.marginTop='2px';
			divLine.appendChild(span22);
			divLine.appendChild(textarea22);
			div.appendChild(divLine);

			var span24 = createSpan("条件")
			var textarea24=createTextarea(function(evt)
			{
				var value =textarea24.value;
				that.addProperty("CompleteCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'CompleteCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea24,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea24,"CompleteCondition_ExpressionStr");
			div.appendChild(span24);
			div.appendChild(textarea24);

			var span26 = createSpan1("终止条件");
			div.appendChild(span26);

            var span28 = createSpan("标识")
            var textarea28=createTextarea(function(evt)
            {
                var value =textarea28.value;
                that.addProperty("DiscardCondition_Cname",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_Cname',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea28,vali)
                }
                console.info(vali);
                graph.addCnameArray(value);
            });
            applyDefalutValue(graph,textarea28,"DiscardCondition_Cname");
            div.appendChild(span28);
            div.appendChild(textarea28);

			var span27 = createSpan("描述")
			var textarea27=createText(function(evt)
			{
				var value =textarea27.value;
				that.addProperty("DiscardCondition_Description",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_Description',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea27,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea27,"DiscardCondition_Description");
			var divLine=document.createElement('div');
            divLine.style.height='40px';
			span27.style.float='left';
			span27.style.height='38px';
			span27.style.lineHeight='38px';
			span27.style.marginTop='2px';
			divLine.appendChild(span27);
			divLine.appendChild(textarea27);
			div.appendChild(divLine);

			var span29 = createSpan("条件")
			var textarea29=createTextarea(function(evt)
			{
				var value =textarea29.value;
				that.addProperty("DiscardCondition_ExpressionStr",value);
                var vali = validateCommonCell(cell.getTypeId(),'DiscardCondition_ExpressionStr',value,graph.getCnameArray());
                if(vali){
                    titleMsg(textarea29,vali)
                }
                console.info(vali);
			});
			applyDefalutValue(graph,textarea29,"DiscardCondition_ExpressionStr");
			div.appendChild(span29);
			div.appendChild(textarea29);

			break;
		default:
			break;
	}
	container.appendChild(div);

  //  applyProperty(graph,input2,"describe")
   function createSpan(name) {
       var span1 = document.createElement('div');
       span1.style.width = '70px';
       span1.style.height = '20px';
       // span1.style.fontWeight = 'bold';
       span1.style.borderImage = "initial";
       span1.style.textAlign = "right";
       span1.style.display = 'inline-block';
       span1.style.lineHeight = '20px';
       span1.style.marginRight='15px';
       span1.style.marginTop='10px';
       mxUtils.write(span1, name);
       return span1;
   };
    function createSpan1(name) {

        var span1 = document.createElement('div');
        span1.style.width = '265px';
        span1.style.height = '20px';
        span1.style.marginTop = '10px';
        span1.style.fontWeight = 'bold';
        span1.style.borderWidth = "1px";
        span1.style.borderColor = "rgb(192, 192, 192)";
        span1.style.borderStyle = "solid";
        span1.style.borderImage = "initial";
        span1.style.textAlign = "center";
        span1.style.fontWeight = "bold";
        span1.style.backgroundColor = "rgb(215, 215, 215)";
        span1.style.display = 'inline-block';
        mxUtils.write(span1, name);
        return span1;
    };
    function createLittleTitle(name) {

        var span1 = document.createElement('div');
        span1.style.width = '80px';
        span1.style.height = '20px';
        span1.style.borderLeft = '2px solid #999';
        span1.style.marginTop = '10px';
        span1.style.marginBottom = '5px';
        span1.style.fontWeight = 'bold';
        span1.style.textAlign = "center";
        span1.style.fontWeight = "bold";
        span1.style.display = 'inline-block';
        mxUtils.write(span1, name);
        return span1;
    };
    function createTitle(name) {

        var span1 = document.createElement('div');
        span1.style.width = '265px';
        span1.style.height = '20px';
        span1.style.paddingTop = '10px';
        span1.style.paddingLeft = '22px';
        span1.style.borderTop='1px dashed #ccc';
        span1.style.marginTop = '10px';
        span1.style.marginBottom = '10px';
        span1.style.fontWeight = 'bold';
        span1.style.borderWidth = "1px";
        span1.style.borderImage = "initial";
        span1.style.textAlign = "left";
        span1.style.fontWeight = "bold";
        span1.style.display = 'inline-block';
        mxUtils.write(span1, name);
        return span1;
    };
   function createRanges(funct1,funct2) {
   		var div=document.createElement('div');
   		var span=createSpan('取值范围');
   		var span1=document.createElement('span');
   		var span2=document.createElement('span');
   		var input1=document.createElement('input');
   		var input2=document.createElement('input');
   		span1.innerHTML='From';
   		span2.innerHTML='To';
   		span1.style.width='20px';
   		span1.style.marginRight='5px';
   		span2.style.width='20px';
   		span1.style.margin='0 5px';
   		input1.style.width='65px';
   		input2.style.width='65px';
   		div.appendChild(span);
   		div.appendChild(span1);
   		div.appendChild(input1);
   		div.appendChild(span2);
   		div.appendChild(input2);
       if(funct1){
           mxEvent.addListener(input1, 'blur', function(evt)
           {
               funct1(evt);
           });
       }
       if(funct2){
           mxEvent.addListener(input2, 'blur', function(evt)
           {
               funct2(evt);
           });
       }
   		return div;
   };
    function createAdd(funct1,funct2,dom,sign) {
        var div=document.createElement('div');
        var span3=document.createElement('span');
        var input1=document.createElement('input');
        var input2=document.createElement('input');
        input1.style.width='95px';
        input1.setAttribute('placeholder','输入分支')
        input1.style.marginLeft='20px';
        input1.style.marginRight='10px';
        input2.style.width='115px';
        input2.setAttribute('placeholder','输入值')
        span3.innerHTML='-';
        span3.style.display='inline-block';
        span3.style.width='20px';
        span3.style.fontSize='20px';
        span3.style.textAlign='center';
        span3.style.cursor='pointer';
        span3.setAttribute('sum',sign)
        span3.onclick=function(){
            div.parentNode.removeChild(div);
            var sum=returnValue (graph,null,'sum');
            var signs=span3.getAttribute('sum');
            dom.removeProperty('CompleteCondition_BranchExpressionStr'+signs);
            dom.removeProperty('CompleteCondition_BranchExpressionValue'+signs);
            // dom.addProperty("sum",sum-1);
        }
        div.appendChild(input1);
        div.appendChild(input2);
        div.appendChild(span3);
        if(funct1){
            mxEvent.addListener(input1, 'blur', function(evt)
            {
                funct1(evt);
            });
        }
        if(funct2){
            mxEvent.addListener(input2, 'blur', function(evt)
            {
                funct2(evt);
            });
        }
        return div;
    };
    function createAddArrange(funct1,funct2,dom,sign) {
        var div=document.createElement('div');
        var span3=document.createElement('span');
        var input1=document.createElement('input');
        var input2=document.createElement('input');
        input1.style.width='70px';
        input1.setAttribute('placeholder','输入名称')
        input1.style.marginLeft='5px';
        input1.style.marginRight='10px';
        input2.style.width='160px';
        input2.setAttribute('placeholder','输入属性值')
        span3.innerHTML='-';
        span3.style.display='inline-block';
        span3.style.width='20px';
        span3.style.fontSize='20px';
        span3.style.textAlign='center';
        span3.style.cursor='pointer';
        span3.setAttribute('sumAttribute',sign)
        span3.onclick=function(){
            div.parentNode.removeChild(div);
            var sumAttribute=returnValue (graph,null,'sumAttribute');
            var signs=span3.getAttribute('sumAttribute');
            dom.removeProperty('MetaAttributeKey'+signs);
            dom.removeProperty('MetaAttributeValue'+signs);
            // dom.addProperty("sum",sum-1);
        }
        div.appendChild(input1);
        div.appendChild(input2);
        div.appendChild(span3);
        if(funct1){
            mxEvent.addListener(input1, 'blur', function(evt)
            {
                funct1(evt);
            });
        }
        if(funct2){
            mxEvent.addListener(input2, 'blur', function(evt)
            {
                funct2(evt);
            });
        }
        return div;
    };
    function createAssets(funct1,funct2,dom,sign) {
        var div=document.createElement('div');
        var span3=document.createElement('span');
        var input1=document.createElement('input');
        var input2=document.createElement('input');
        input1.style.width='70px';
        input1.setAttribute('placeholder','输入名称')
        input1.style.marginLeft='5px';
        input1.style.marginRight='10px';
        input2.style.width='160px';
        input2.setAttribute('placeholder','输入属性值')
        span3.innerHTML='-';
        span3.style.display='inline-block';
        span3.style.width='20px';
        span3.style.fontSize='20px';
        span3.style.textAlign='center';
        span3.style.cursor='pointer';
        span3.setAttribute('sumAssets',sign)
        span3.onclick=function(){
            div.parentNode.removeChild(div);
            var sumAssets=returnValue (graph,null,'sumAssets');
            var signs=span3.getAttribute('sumAssets');
            dom.removeProperty('ContractAssetsKey'+signs);
            dom.removeProperty('ContractAssetsValue'+signs);
            // dom.addProperty("sum",sum-1);
        }
        div.appendChild(input1);
        div.appendChild(input2);
        div.appendChild(span3);
        if(funct1){
            mxEvent.addListener(input1, 'blur', function(evt)
            {
                funct1(evt);
            });
        }
        if(funct2){
            mxEvent.addListener(input2, 'blur', function(evt)
            {
                funct2(evt);
            });
        }
        return div;
    };
    function createText(funct){
        var textarea = document.createElement("textarea");
        mxEvent.addListener(textarea, 'click', function(evt)
        {
            textarea.focus();
        });
        if(funct){
            mxEvent.addListener(textarea, 'blur', function(evt)
            {
                funct(evt);
            });
        }
        textarea.id = 'textarea';
        textarea.disable = 'value';
        textarea.zindex = '999';
        textarea.style.width = '180px';
        textarea.style.wordWrap = 'break';
        textarea.style.height = '38px';
        textarea.style.marginTop = '10px';
        textarea.style.fontWeight = 'bold';
        textarea.style.borderWidth = "1px";
        textarea.style.borderColor = "rgb(192, 192, 192)";
        textarea.style.borderStyle = "solid";
        textarea.style.borderImage = "initial";
        textarea.style.textAlign = "left";
        textarea.style.fontWeight = "bold";

        return textarea;
    }
	function titleMsg(dom,vali) {
        var oTitle=document.createElement('div');
        oTitle.setAttribute('class','arrow_box')
        oTitle.style.width='180px';
        oTitle.style.padding='10px';
        oTitle.style.color='#080808';
        oTitle.innerHTML=vali;
        div.appendChild(oTitle);
        oTitle.style.position='absolute';
        oTitle.style.left=dom.offsetLeft+'px';
        oTitle.style.top=(dom.offsetTop-oTitle.offsetHeight-dom.offsetHeight+7)+'px';
        setTimeout(function () {

            div.removeChild(oTitle);
        },3000)
    }
   function createTextarea(funct) {
       var textarea = document.createElement('input');
       if(funct){
           mxEvent.addListener(textarea, 'blur', function(evt)
           {
               funct(evt);
           });
       }
       // textarea.rows='2';
       // textarea.cols='10';
       textarea.style.width = '180px';
       textarea.style.wordWrap = 'break';
       textarea.style.height = '18px';
       textarea.style.marginTop = '10px';
       textarea.style.fontWeight = 'bold';
       textarea.style.borderWidth = "1px";
       textarea.style.borderColor = "rgb(192, 192, 192)";
       textarea.style.borderStyle = "solid";
       textarea.style.borderImage = "initial";
       textarea.style.textAlign = "left";
       textarea.style.fontWeight = "bold";

       return textarea;
   };
   function createOptions(arry,funct) {
       var styleSelect = document.createElement('select');
       styleSelect.style.width = '180px';

       if(funct){
           mxEvent.addListener(styleSelect, 'change', function(evt)
           {
               funct(evt);
           });
       }
       var styles = arry ;

       for (var i = 0; i < styles.length; i++)
       {
           var styleOption = document.createElement('option');
           styleOption.setAttribute('value', styles[i].name);
           mxUtils.write(styleOption, styles[i].text);
           styleSelect.appendChild(styleOption);
       }
       return styleSelect;
   }
   function createTextarea1(funct) {
       var textarea = document.createElement('input');
       if(funct){
           mxEvent.addListener(textarea, 'blur', function(evt)
           {
               funct(evt);
           });
       }
       // textarea.rows='2';
       // textarea.cols='10';
       textarea.style.width = '180px';
       textarea.style.wordWrap = 'break';
       textarea.style.height = '18px';
       textarea.style.marginTop = '10px';
       textarea.style.fontWeight = 'bold';
       textarea.style.borderWidth = "1px";
       textarea.style.borderColor = "rgb(192, 192, 192)";
       textarea.style.borderStyle = "solid";
       textarea.style.borderImage = "initial";
       textarea.style.textAlign = "left";
       textarea.style.fontWeight = "bold";

       return textarea;
   };
   function createCheckbox(funct) {

       var checkbox = document.createElement('input');
       if(funct){
           mxEvent.addListener(checkbox, 'click', function(evt)
           {
               funct(evt);
           });
       }
       checkbox.style.whiteSpace = 'nowrap';
       checkbox.style.overflow = 'hidden';
       checkbox.style.height = '15px';
       checkbox.setAttribute('type', 'checkbox');
       return checkbox;
   };
   function applyDefalutValue (graph,dom,attribute){
       var cell = graph.getSelectionCell() || graph.getModel().getRoot();
       var labelValue = cell.value;
       if(!labelValue){
           dom.value = "";
       }else{
           if(Object.prototype.toString.call(labelValue)== "[object String]" ){
               dom.value = labelValue;
           }
           else{
               dom.value = labelValue.getAttribute(attribute);
           }
       }
   }   
   function returnValue (graph,dom,attribute){
       var cell = graph.getSelectionCell() || graph.getModel().getRoot();
       var labelValue = cell.value;
       if(!labelValue){
       }else{
           if(Object.prototype.toString.call(labelValue)== "[object String]" ){
           }
           else{
        		return labelValue.getAttribute(attribute);
           }
       }
   }
   function applyDefalutValue1 (graph,dom,attribute){
       var cell = graph.getSelectionCell() || graph.getModel().getRoot();
       var labelValue = cell.value;
//     console.log(labelValue);
       if(!labelValue){
           dom.value = "";
       }else{
           if(Object.prototype.toString.call(labelValue)== "[object String]" ){
               dom.value = labelValue;
           }
           else{
        		// console.log(labelValue);
               dom.value = labelValue.getAttribute('label');
           }
       }
   }
   function applyProperty(graph,dom,attribute) {
       var cell = graph.getSelectionCell() || graph.getModel().getRoot();
       var labelValue = cell.value;
	   if(Object.prototype.toString.call(labelValue)== "[object Element]" )
		   dom.value = labelValue.getAttribute(attribute);
	   else
		   dom.value = "";
   }
   // debugger;


}


ArrangePanel.prototype.addLayerOps = function(div)
{
	var ui = this.editorUi;
	
	var btn = mxUtils.button(mxResources.get('toFront'), function(evt)
	{
		ui.actions.get('toFront').funct();
	})
	
	btn.setAttribute('title', mxResources.get('toFront') + ' (' + this.editorUi.actions.get('toFront').shortcut + ')');
	btn.style.width = '100px';
	btn.style.marginRight = '2px';
	div.appendChild(btn);
	
	var btn = mxUtils.button(mxResources.get('toBack'), function(evt)
	{
		ui.actions.get('toBack').funct();
	})
	
	btn.setAttribute('title', mxResources.get('toBack') + ' (' + this.editorUi.actions.get('toBack').shortcut + ')');
	btn.style.width = '100px';
	div.appendChild(btn);
	
	return div;
};

/**
 * 
 */
ArrangePanel.prototype.addGroupOps = function(div)
{
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	var cell = graph.getSelectionCell();
	var ss = this.format.getSelectionState();
	var count = 0;
	
	div.style.paddingTop = '8px';
	div.style.paddingBottom = '6px';

	if (graph.getSelectionCount() == 1)
	{
		if (count > 0)
		{
			mxUtils.br(div);
		}
		btn = mxUtils.button(mxResources.get('editData'), mxUtils.bind(this, function(evt)
		{
			this.editorUi.actions.get('editData').funct();
		}));
		
		btn.setAttribute('title', mxResources.get('editData') + ' (' + this.editorUi.actions.get('editData').shortcut + ')');
		btn.style.width = '265px';
		btn.style.marginBottom = '2px';
		div.appendChild(btn);
		count++;

	}
	
	if (count == 0)
	{
		div.style.display = 'none';
	}
	return div;
};

/**
 * 
 */
ArrangePanel.prototype.addAlign = function(div)
{
	var graph = this.editorUi.editor.graph;
	div.style.paddingTop = '6px';
	div.style.paddingBottom = '12px';
	div.appendChild(this.createTitle(mxResources.get('align')));
	
	var stylePanel = document.createElement('div');
	stylePanel.style.position = 'relative';
	stylePanel.style.paddingLeft = '0px';
	stylePanel.style.borderWidth = '0px';
	stylePanel.className = 'geToolbarContainer';
	
	if (mxClient.IS_QUIRKS)
	{
		div.style.height = '60px';
	}
	
	var left = this.editorUi.toolbar.addButton('geSprite-alignleft', mxResources.get('left'),
		function() { graph.alignCells(mxConstants.ALIGN_LEFT); }, stylePanel);
	var center = this.editorUi.toolbar.addButton('geSprite-aligncenter', mxResources.get('center'),
		function() { graph.alignCells(mxConstants.ALIGN_CENTER); }, stylePanel);
	var right = this.editorUi.toolbar.addButton('geSprite-alignright', mxResources.get('right'),
		function() { graph.alignCells(mxConstants.ALIGN_RIGHT); }, stylePanel);

	var top = this.editorUi.toolbar.addButton('geSprite-aligntop', mxResources.get('top'),
		function() { graph.alignCells(mxConstants.ALIGN_TOP); }, stylePanel);
	var middle = this.editorUi.toolbar.addButton('geSprite-alignmiddle', mxResources.get('middle'),
		function() { graph.alignCells(mxConstants.ALIGN_MIDDLE); }, stylePanel);
	var bottom = this.editorUi.toolbar.addButton('geSprite-alignbottom', mxResources.get('bottom'),
		function() { graph.alignCells(mxConstants.ALIGN_BOTTOM); }, stylePanel);
	
	this.styleButtons([left, center, right, top, middle, bottom]);
	right.style.marginRight = '6px';
	div.appendChild(stylePanel);
	
	return div;
};

/**
 * 
 */
ArrangePanel.prototype.addFlip = function(div)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	div.style.paddingTop = '6px';
	div.style.paddingBottom = '10px';

	var span = document.createElement('div');
	span.style.marginTop = '2px';
	span.style.marginBottom = '8px';
	span.style.fontWeight = 'bold';
	mxUtils.write(span, mxResources.get('flip'));
	div.appendChild(span);
	
	var btn = mxUtils.button(mxResources.get('horizontal'), function(evt)
	{
		graph.toggleCellStyles(mxConstants.STYLE_FLIPH, false);
	})
	
	btn.setAttribute('title', mxResources.get('horizontal'));
	btn.style.width = '100px';
	btn.style.marginRight = '2px';
	div.appendChild(btn);
	
	var btn = mxUtils.button(mxResources.get('vertical'), function(evt)
	{
		graph.toggleCellStyles(mxConstants.STYLE_FLIPV, false);
	})
	
	btn.setAttribute('title', mxResources.get('vertical'));
	btn.style.width = '100px';
	div.appendChild(btn);
	
	return div;
};

/**
 * 
 */
ArrangePanel.prototype.addDistribute = function(div)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	div.style.paddingTop = '6px';
	div.style.paddingBottom = '12px';
	
	div.appendChild(this.createTitle(mxResources.get('distribute')));

	var btn = mxUtils.button(mxResources.get('horizontal'), function(evt)
	{
		graph.distributeCells(true);
	})
	
	btn.setAttribute('title', mxResources.get('horizontal'));
	btn.style.width = '100px';
	btn.style.marginRight = '2px';
	div.appendChild(btn);
	
	var btn = mxUtils.button(mxResources.get('vertical'), function(evt)
	{
		graph.distributeCells(false);
	})
	
	btn.setAttribute('title', mxResources.get('vertical'));
	btn.style.width = '100px';
	div.appendChild(btn);
	
	return div;
};

/**
 * 
 */
ArrangePanel.prototype.addAngle = function(div)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	var ss = this.format.getSelectionState();

	div.style.paddingBottom = '8px';
	
	var span = document.createElement('div');
	span.style.position = 'absolute';
	span.style.width = '70px';
	span.style.marginTop = '0px';
	span.style.fontWeight = 'bold';
	
	var input = null;
	var update = null;
	var btn = null;
	
	if (ss.edges.length == 0)
	{
		mxUtils.write(span, mxResources.get('angle'));
		div.appendChild(span);
		
		input = this.addUnitInput(div, '°', 20, 44, function()
		{
			update.apply(this, arguments);
		});
		
		mxUtils.br(div);
		div.style.paddingTop = '10px';
	}
	else
	{
		div.style.paddingTop = '8px';
	}

	if (!ss.containsLabel)
	{
		var label = mxResources.get('reverse');
		
		if (ss.vertices.length > 0 && ss.edges.length > 0)
		{
			label = mxResources.get('turn') + ' / ' + label;
		}
		else if (ss.vertices.length > 0)
		{
			label = mxResources.get('turn');
		}

		btn = mxUtils.button(label, function(evt)
		{
			ui.actions.get('turn').funct();
		})
		
		btn.setAttribute('title', label + ' (' + this.editorUi.actions.get('turn').shortcut + ')');
		btn.style.width = '202px';
		div.appendChild(btn);
		
		if (input != null)
		{
			btn.style.marginTop = '8px';
		}
	}
	
	if (input != null)
	{
		var listener = mxUtils.bind(this, function(sender, evt, force)
		{
			if (force || document.activeElement != input)
			{
				ss = this.format.getSelectionState();
				var tmp = parseFloat(mxUtils.getValue(ss.style, mxConstants.STYLE_ROTATION, 0));
				input.value = (isNaN(tmp)) ? '' : tmp  + '°';
			}
		});
	
		update = this.installInputHandler(input, mxConstants.STYLE_ROTATION, 0, 0, 360, '°', null, true);
		this.addKeyHandler(input, listener);
	
		graph.getModel().addListener(mxEvent.CHANGE, listener);
		this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
		listener();
	}

	return div;
};

/**
 * 
 */
ArrangePanel.prototype.addGeometry = function(container)
{
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	var rect = this.format.getSelectionState();
	
	var div = this.createPanel();
	div.style.paddingBottom = '8px';
	
	var span = document.createElement('div');
	span.style.position = 'absolute';
	span.style.width = '50px';
	span.style.marginTop = '0px';
	span.style.fontWeight = 'bold';
	mxUtils.write(span, mxResources.get('size'));
	div.appendChild(span);

	var widthUpdate, heightUpdate, leftUpdate, topUpdate;
	var width = this.addUnitInput(div, 'pt', 84, 44, function()
	{
		widthUpdate.apply(this, arguments);
	});
	var height = this.addUnitInput(div, 'pt', 20, 44, function()
	{
		heightUpdate.apply(this, arguments);
	});
	
	var autosizeBtn = document.createElement('div');
	autosizeBtn.className = 'geSprite geSprite-fit';
	autosizeBtn.setAttribute('title', mxResources.get('autosize') + ' (' + this.editorUi.actions.get('autosize').shortcut + ')');
	autosizeBtn.style.position = 'relative';
	autosizeBtn.style.cursor = 'pointer';
	autosizeBtn.style.marginTop = '-3px';
	autosizeBtn.style.border = '0px';
	autosizeBtn.style.left = '52px';
	mxUtils.setOpacity(autosizeBtn, 50);
	
	mxEvent.addListener(autosizeBtn, 'mouseenter', function()
	{
		mxUtils.setOpacity(autosizeBtn, 100);
	});
	
	mxEvent.addListener(autosizeBtn, 'mouseleave', function()
	{
		mxUtils.setOpacity(autosizeBtn, 50);
	});

	mxEvent.addListener(autosizeBtn, 'click', function()
	{
		ui.actions.get('autosize').funct();
	});
	
	div.appendChild(autosizeBtn);
	this.addLabel(div, mxResources.get('width'), 84);
	this.addLabel(div, mxResources.get('height'), 20);
	mxUtils.br(div);
	
	var wrapper = document.createElement('div');
	wrapper.style.paddingTop = '8px';
	wrapper.style.paddingRight = '20px';
	wrapper.style.whiteSpace = 'nowrap';
	wrapper.style.textAlign = 'right';
	var opt = this.createCellOption(mxResources.get('constrainProportions'),
		mxConstants.STYLE_ASPECT, null, 'fixed', 'null');
	opt.style.width = '100%';
	wrapper.appendChild(opt);
	div.appendChild(wrapper);
	
	this.addKeyHandler(width, listener);
	this.addKeyHandler(height, listener);

	widthUpdate = this.addGeometryHandler(width, function(geo, value)
	{
		if (geo.width > 0)
		{
			geo.width = Math.max(1, value);
		}
	});
	heightUpdate = this.addGeometryHandler(height, function(geo, value)
	{
		if (geo.height > 0)
		{
			geo.height = Math.max(1, value);
		}
	});
	
	container.appendChild(div);
	
	var div2 = this.createPanel();
	div2.style.paddingBottom = '30px';
	
	var span = document.createElement('div');
	span.style.position = 'absolute';
	span.style.width = '70px';
	span.style.marginTop = '0px';
	span.style.fontWeight = 'bold';
	mxUtils.write(span, mxResources.get('position'));
	div2.appendChild(span);
	
	var left = this.addUnitInput(div2, 'pt', 84, 44, function()
	{
		leftUpdate.apply(this, arguments);
	});
	var top = this.addUnitInput(div2, 'pt', 20, 44, function()
	{
		topUpdate.apply(this, arguments);
	});

	mxUtils.br(div2);
	this.addLabel(div2, mxResources.get('left'), 84);
	this.addLabel(div2, mxResources.get('top'), 20);
	
	var listener = mxUtils.bind(this, function(sender, evt, force)
	{
		rect = this.format.getSelectionState();

		if (!rect.containsLabel && rect.vertices.length == graph.getSelectionCount() &&
			rect.width != null && rect.height != null)
		{
			div.style.display = '';
			
			if (force || document.activeElement != width)
			{
				width.value = rect.width + ((rect.width == '') ? '' : ' pt');
			}
			
			if (force || document.activeElement != height)
			{
				height.value = rect.height + ((rect.height == '') ? '' : ' pt');
			}
		}
		else
		{
			div.style.display = 'none';
		}
		
		if (rect.vertices.length == graph.getSelectionCount() &&
			rect.x != null && rect.y != null)
		{
			div2.style.display = '';
			
			if (force || document.activeElement != left)
			{
				left.value = rect.x  + ((rect.x == '') ? '' : ' pt');
			}
			
			if (force || document.activeElement != top)
			{
				top.value = rect.y + ((rect.y == '') ? '' : ' pt');
			}
		}
		else
		{
			div2.style.display = 'none';
		}
	});

	this.addKeyHandler(left, listener);
	this.addKeyHandler(top, listener);

	graph.getModel().addListener(mxEvent.CHANGE, listener);
	this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
	listener();
	
	leftUpdate = this.addGeometryHandler(left, function(geo, value)
	{
		if (geo.relative)
		{
			geo.offset.x = value;
		}
		else
		{
			geo.x = value;
		}
	});
	topUpdate = this.addGeometryHandler(top, function(geo, value)
	{
		if (geo.relative)
		{
			geo.offset.y = value;
		}
		else
		{
			geo.y = value;
		}
	});

	container.appendChild(div2);
};

/**
 * 
 */
ArrangePanel.prototype.addGeometryHandler = function(input, fn)
{
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	var initialValue = null;
	
	function update(evt)
	{
		if (input.value != '')
		{
			var value = parseFloat(input.value);

			if (value != initialValue)
			{
				graph.getModel().beginUpdate();
				try
				{
					var cells = graph.getSelectionCells();
					
					for (var i = 0; i < cells.length; i++)
					{
						if (graph.getModel().isVertex(cells[i]))
						{
							var geo = graph.getCellGeometry(cells[i]);
							
							if (geo != null)
							{
								geo = geo.clone();
								fn(geo, value);
								
								graph.getModel().setGeometry(cells[i], geo);
							}
						}
					}
				}
				finally
				{
					graph.getModel().endUpdate();
				}
				
				initialValue = value;
				input.value = value + ' pt';
			}
			else if (isNaN(value)) 
			{
				input.value = initialValue + ' pt';
			}
		}
		
		mxEvent.consume(evt);
	};

	mxEvent.addListener(input, 'blur', update);
	mxEvent.addListener(input, 'change', update);
	mxEvent.addListener(input, 'focus', function()
	{
		initialValue = input.value;
	});
	
	return update;
};

/**
 * 
 */
ArrangePanel.prototype.addEdgeGeometry = function(container)
{
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	var rect = this.format.getSelectionState();
	
	var div = this.createPanel();
	
	var span = document.createElement('div');
	span.style.position = 'absolute';
	span.style.width = '70px';
	span.style.marginTop = '0px';
	span.style.fontWeight = 'bold';
	mxUtils.write(span, mxResources.get('width'));
	div.appendChild(span);

	var widthUpdate, leftUpdate, topUpdate;
	var width = this.addUnitInput(div, 'pt', 20, 44, function()
	{
		widthUpdate.apply(this, arguments);
	});

	mxUtils.br(div);
	this.addKeyHandler(width, listener);
	
	function widthUpdate(evt)
	{
		// Maximum stroke width is 999
		var value = parseInt(width.value);
		value = Math.min(999, Math.max(1, (isNaN(value)) ? 1 : value));
		
		if (value != mxUtils.getValue(rect.style, 'width', mxCellRenderer.prototype.defaultShapes['flexArrow'].prototype.defaultWidth))
		{
			graph.setCellStyles('width', value, graph.getSelectionCells());
			ui.fireEvent(new mxEventObject('styleChanged', 'keys', ['width'],
					'values', [value], 'cells', graph.getSelectionCells()));
		}

		width.value = value + ' pt';
		mxEvent.consume(evt);
	};

	mxEvent.addListener(width, 'blur', widthUpdate);
	mxEvent.addListener(width, 'change', widthUpdate);

	container.appendChild(div);
	
	var listener = mxUtils.bind(this, function(sender, evt, force)
	{
		rect = this.format.getSelectionState();
		
		if (rect.style.shape == 'link' || rect.style.shape == 'flexArrow')
		{
			div.style.display = '';
			
			if (force || document.activeElement != width)
			{
				var value = mxUtils.getValue(rect.style, 'width',
					mxCellRenderer.prototype.defaultShapes['flexArrow'].prototype.defaultWidth);
				width.value = value + ' pt';
			}
		}
		else
		{
			div.style.display = 'none';
		}
	});

	graph.getModel().addListener(mxEvent.CHANGE, listener);
	this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
	listener();
};

/**
 * Adds the label menu items to the given menu and parent.
 */
TextFormatPanel = function(format, editorUi, container)
{
	BaseFormatPanel.call(this, format, editorUi, container);
	this.init();
};

mxUtils.extend(TextFormatPanel, BaseFormatPanel);

/**
 * Adds the label menu items to the given menu and parent.
 */
TextFormatPanel.prototype.init = function()
{
	this.container.style.borderBottom = 'none';
	this.addFont(this.container);
};

/**
 * Adds the label menu items to the given menu and parent.
 */
TextFormatPanel.prototype.addFont = function(container)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	var ss = this.format.getSelectionState();
	
	var title = this.createTitle(mxResources.get('font'));
	title.style.paddingLeft = '18px';
	title.style.paddingTop = '10px';
	title.style.paddingBottom = '6px';
	container.appendChild(title);

	var stylePanel = this.createPanel();
	stylePanel.style.paddingTop = '2px';
	stylePanel.style.paddingBottom = '2px';
	stylePanel.style.position = 'relative';
	stylePanel.style.marginLeft = '-2px';
	stylePanel.style.borderWidth = '0px';
	stylePanel.className = 'geToolbarContainer';
	
	if (mxClient.IS_QUIRKS)
	{
		stylePanel.style.display = 'block';
	}

	if (graph.cellEditor.isContentEditing())
	{
		var cssPanel = stylePanel.cloneNode();
		
		var cssMenu = this.editorUi.toolbar.addMenu(mxResources.get('style'),
			mxResources.get('style'), true, 'formatBlock', cssPanel);
		cssMenu.style.color = 'rgb(112, 112, 112)';
		cssMenu.style.whiteSpace = 'nowrap';
		cssMenu.style.overflow = 'hidden';
		cssMenu.style.margin = '0px';
		this.addArrow(cssMenu);
		cssMenu.style.width = '192px';
		cssMenu.style.height = '15px';
		
		var arrow = cssMenu.getElementsByTagName('div')[0];
		arrow.style.cssFloat = 'right';
		container.appendChild(cssPanel);
	}
	
	container.appendChild(stylePanel);
	
	var colorPanel = this.createPanel();
	colorPanel.style.marginTop = '8px';
	colorPanel.style.borderTop = '1px solid #c0c0c0';
	colorPanel.style.paddingTop = '6px';
	colorPanel.style.paddingBottom = '6px';

	var fontMenu = this.editorUi.toolbar.addMenu('Helvetica', mxResources.get('fontFamily'), true, 'fontFamily', stylePanel);
	fontMenu.style.color = 'rgb(112, 112, 112)';
	fontMenu.style.whiteSpace = 'nowrap';
	fontMenu.style.overflow = 'hidden';
	fontMenu.style.margin = '0px';
	
	this.addArrow(fontMenu);
	fontMenu.style.width = '192px';
	fontMenu.style.height = '15px';
	
	// Workaround for offset in FF
	if (mxClient.IS_FF)
	{
		fontMenu.getElementsByTagName('div')[0].style.marginTop = '-18px';
	}
	
	var stylePanel2 = stylePanel.cloneNode(false);
	stylePanel2.style.marginLeft = '-3px';
	var fontStyleItems = this.editorUi.toolbar.addItems(['bold', 'italic', 'underline'], stylePanel2, true);
	fontStyleItems[0].setAttribute('title', mxResources.get('bold') + ' (' + this.editorUi.actions.get('bold').shortcut + ')');
	fontStyleItems[1].setAttribute('title', mxResources.get('italic') + ' (' + this.editorUi.actions.get('italic').shortcut + ')');
	fontStyleItems[2].setAttribute('title', mxResources.get('underline') + ' (' + this.editorUi.actions.get('underline').shortcut + ')');
	
	var verticalItem = this.editorUi.toolbar.addItems(['vertical'], stylePanel2, true)[0];
	
	if (mxClient.IS_QUIRKS)
	{
		mxUtils.br(container);
	}
	
	container.appendChild(stylePanel2);

	this.styleButtons(fontStyleItems);
	this.styleButtons([verticalItem]);
	
	var stylePanel3 = stylePanel.cloneNode(false);
	stylePanel3.style.marginLeft = '-3px';
	stylePanel3.style.paddingBottom = '0px';
	
	var left = this.editorUi.toolbar.addButton('geSprite-left', mxResources.get('left'),
			(graph.cellEditor.isContentEditing()) ?
			function()
			{
				document.execCommand('justifyleft', false, null);
			} : this.editorUi.menus.createStyleChangeFunction([mxConstants.STYLE_ALIGN], [mxConstants.ALIGN_LEFT]), stylePanel3);
	var center = this.editorUi.toolbar.addButton('geSprite-center', mxResources.get('center'),
			(graph.cellEditor.isContentEditing()) ?
			function()
			{
				document.execCommand('justifycenter', false, null);
			} : this.editorUi.menus.createStyleChangeFunction([mxConstants.STYLE_ALIGN], [mxConstants.ALIGN_CENTER]), stylePanel3);
	var right = this.editorUi.toolbar.addButton('geSprite-right', mxResources.get('right'),
			(graph.cellEditor.isContentEditing()) ?
			function()
			{
				document.execCommand('justifyright', false, null);
			} : this.editorUi.menus.createStyleChangeFunction([mxConstants.STYLE_ALIGN], [mxConstants.ALIGN_RIGHT]), stylePanel3);

	this.styleButtons([left, center, right]);

	if (graph.cellEditor.isContentEditing())
	{
		var clear = this.editorUi.toolbar.addButton('geSprite-removeformat', mxResources.get('removeFormat'),
			function()
			{
				document.execCommand('removeformat', false, null);
			}, stylePanel2);
		this.styleButtons([clear]);
	}
	
	var top = this.editorUi.toolbar.addButton('geSprite-top', mxResources.get('top'),
		this.editorUi.menus.createStyleChangeFunction([mxConstants.STYLE_VERTICAL_ALIGN], [mxConstants.ALIGN_TOP]), stylePanel3);
	var middle = this.editorUi.toolbar.addButton('geSprite-middle', mxResources.get('middle'),
		this.editorUi.menus.createStyleChangeFunction([mxConstants.STYLE_VERTICAL_ALIGN], [mxConstants.ALIGN_MIDDLE]), stylePanel3);
	var bottom = this.editorUi.toolbar.addButton('geSprite-bottom', mxResources.get('bottom'),
		this.editorUi.menus.createStyleChangeFunction([mxConstants.STYLE_VERTICAL_ALIGN], [mxConstants.ALIGN_BOTTOM]), stylePanel3);
	
	this.styleButtons([top, middle, bottom]);
	
	if (mxClient.IS_QUIRKS)
	{
		mxUtils.br(container);
	}
	
	container.appendChild(stylePanel3);
	
	// Hack for updating UI state below based on current text selection
	// currentTable is the current selected DOM table updated below
	var sub, sup, full, tableWrapper, currentTable, tableCell, tableRow;
	
	if (graph.cellEditor.isContentEditing())
	{
		top.style.display = 'none';
		middle.style.display = 'none';
		bottom.style.display = 'none';
		verticalItem.style.display = 'none';
		
		full = this.editorUi.toolbar.addButton('geSprite-justifyfull', null,
			function()
			{
				document.execCommand('justifyfull', false, null);
			}, stylePanel3);
		this.styleButtons([full,
       		sub = this.editorUi.toolbar.addButton('geSprite-subscript', mxResources.get('subscript') + ' (Ctrl+,)',
			function()
			{
				document.execCommand('subscript', false, null);
			}, stylePanel3), sup = this.editorUi.toolbar.addButton('geSprite-superscript', mxResources.get('superscript') + ' (Ctrl+.)',
			function()
			{
				document.execCommand('superscript', false, null);
			}, stylePanel3)]);
		full.style.marginRight = '9px';
		
		var tmp = stylePanel3.cloneNode(false);
		tmp.style.paddingTop = '4px';
		var btns = [this.editorUi.toolbar.addButton('geSprite-orderedlist', mxResources.get('numberedList'),
				function()
				{
					document.execCommand('insertorderedlist', false, null);
				}, tmp),
			this.editorUi.toolbar.addButton('geSprite-unorderedlist', mxResources.get('bulletedList'),
				function()
				{
					document.execCommand('insertunorderedlist', false, null);
				}, tmp),
			this.editorUi.toolbar.addButton('geSprite-outdent', mxResources.get('decreaseIndent'),
					function()
					{
						document.execCommand('outdent', false, null);
					}, tmp),
			this.editorUi.toolbar.addButton('geSprite-indent', mxResources.get('increaseIndent'),
				function()
				{
					document.execCommand('indent', false, null);
				}, tmp),
			this.editorUi.toolbar.addButton('geSprite-code', mxResources.get('html'),
				function()
				{
					graph.cellEditor.toggleViewMode();
				}, tmp)];
		this.styleButtons(btns);
		btns[btns.length - 1].style.marginLeft = '9px';
		
		if (mxClient.IS_QUIRKS)
		{
			mxUtils.br(container);
			tmp.style.height = '40';
		}
		
		container.appendChild(tmp);
	}
	else
	{
		fontStyleItems[2].style.marginRight = '9px';
		right.style.marginRight = '9px';
	}
	
	// Label position
	var stylePanel4 = stylePanel.cloneNode(false);
	stylePanel4.style.marginLeft = '0px';
	stylePanel4.style.paddingTop = '8px';
	stylePanel4.style.paddingBottom = '4px';
	stylePanel4.style.fontWeight = 'normal';
	
	mxUtils.write(stylePanel4, mxResources.get('position'));
	
	// Adds label position options
	var positionSelect = document.createElement('select');
	positionSelect.style.position = 'absolute';
	positionSelect.style.right = '20px';
	positionSelect.style.width = '97px';
	positionSelect.style.marginTop = '-2px';
	
	var directions = ['topLeft', 'top', 'topRight', 'left', 'center', 'right', 'bottomLeft', 'bottom', 'bottomRight'];
	var lset = {'topLeft': [mxConstants.ALIGN_LEFT, mxConstants.ALIGN_TOP, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_BOTTOM],
			'top': [mxConstants.ALIGN_CENTER, mxConstants.ALIGN_TOP, mxConstants.ALIGN_CENTER, mxConstants.ALIGN_BOTTOM],
			'topRight': [mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP, mxConstants.ALIGN_LEFT, mxConstants.ALIGN_BOTTOM],
			'left': [mxConstants.ALIGN_LEFT, mxConstants.ALIGN_MIDDLE, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_MIDDLE],
			'center': [mxConstants.ALIGN_CENTER, mxConstants.ALIGN_MIDDLE, mxConstants.ALIGN_CENTER, mxConstants.ALIGN_MIDDLE],
			'right': [mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_MIDDLE, mxConstants.ALIGN_LEFT, mxConstants.ALIGN_MIDDLE],
			'bottomLeft': [mxConstants.ALIGN_LEFT, mxConstants.ALIGN_BOTTOM, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP],
			'bottom': [mxConstants.ALIGN_CENTER, mxConstants.ALIGN_BOTTOM, mxConstants.ALIGN_CENTER, mxConstants.ALIGN_TOP],
			'bottomRight': [mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_BOTTOM, mxConstants.ALIGN_LEFT, mxConstants.ALIGN_TOP]};

	for (var i = 0; i < directions.length; i++)
	{
		var positionOption = document.createElement('option');
		positionOption.setAttribute('value', directions[i]);
		mxUtils.write(positionOption, mxResources.get(directions[i]));
		positionSelect.appendChild(positionOption);
	}

	stylePanel4.appendChild(positionSelect);
	
	// Writing direction
	var stylePanel5 = stylePanel.cloneNode(false);
	stylePanel5.style.marginLeft = '0px';
	stylePanel5.style.paddingTop = '4px';
	stylePanel5.style.paddingBottom = '4px';
	stylePanel5.style.fontWeight = 'normal';

	mxUtils.write(stylePanel5, mxResources.get('writingDirection'));
	
	// Adds writing direction options
	// LATER: Handle reselect of same option in all selects (change event
	// is not fired for same option so have opened state on click) and
	// handle multiple different styles for current selection
	var dirSelect = document.createElement('select');
	dirSelect.style.position = 'absolute';
	dirSelect.style.right = '20px';
	dirSelect.style.width = '97px';
	dirSelect.style.marginTop = '-2px';

	// NOTE: For automatic we use the value null since automatic
	// requires the text to be non formatted and non-wrapped
	var dirs = ['automatic', 'leftToRight', 'rightToLeft'];
	var dirSet = {'automatic': null,
			'leftToRight': mxConstants.TEXT_DIRECTION_LTR,
			'rightToLeft': mxConstants.TEXT_DIRECTION_RTL};

	for (var i = 0; i < dirs.length; i++)
	{
		var dirOption = document.createElement('option');
		dirOption.setAttribute('value', dirs[i]);
		mxUtils.write(dirOption, mxResources.get(dirs[i]));
		dirSelect.appendChild(dirOption);
	}

	stylePanel5.appendChild(dirSelect);
	
	if (!graph.isEditing())
	{
		container.appendChild(stylePanel4);
		
		mxEvent.addListener(positionSelect, 'change', function(evt)
		{
			graph.getModel().beginUpdate();
			try
			{
				var vals = lset[positionSelect.value];
				
				if (vals != null)
				{
					graph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, vals[0], graph.getSelectionCells());
					graph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, vals[1], graph.getSelectionCells());
					graph.setCellStyles(mxConstants.STYLE_ALIGN, vals[2], graph.getSelectionCells());
					graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN, vals[3], graph.getSelectionCells());
				}
			}
			finally
			{
				graph.getModel().endUpdate();
			}
			
			mxEvent.consume(evt);
		});

		// LATER: Update dir in text editor while editing and update style with label
		// NOTE: The tricky part is handling and passing on the auto value
		container.appendChild(stylePanel5);
		
		mxEvent.addListener(dirSelect, 'change', function(evt)
		{
			graph.setCellStyles(mxConstants.STYLE_TEXT_DIRECTION, dirSet[dirSelect.value], graph.getSelectionCells());
			mxEvent.consume(evt);
		});
	}

	// Font size
	var input = document.createElement('input');
	input.style.textAlign = 'right';
	input.style.marginTop = '4px';
	
	if (!mxClient.IS_QUIRKS)
	{
		input.style.position = 'absolute';
		input.style.right = '32px';
	}
	
	input.style.width = '46px';
	input.style.height = (mxClient.IS_QUIRKS) ? '21px' : '17px';
	stylePanel2.appendChild(input);
	
	// Workaround for font size 4 if no text is selected is update font size below
	// after first character was entered (as the font element is lazy created)
	var pendingFontSize = null;

	var inputUpdate = this.installInputHandler(input, mxConstants.STYLE_FONTSIZE, Menus.prototype.defaultFontSize, 1, 999, ' pt',
	function(fontsize)
	{
		pendingFontSize = fontsize;

		// Workaround for can't set font size in px is to change font size afterwards
		document.execCommand('fontSize', false, '4');
		var elts = graph.cellEditor.textarea.getElementsByTagName('font');
		
		for (var i = 0; i < elts.length; i++)
		{
			if (elts[i].getAttribute('size') == '4')
			{
				elts[i].removeAttribute('size');
				elts[i].style.fontSize = pendingFontSize + 'px';
	
				// Overrides fontSize in input with the one just assigned as a workaround
				// for potential fontSize values of parent elements that don't match
				window.setTimeout(function()
				{
					input.value = pendingFontSize + ' pt';
					pendingFontSize = null;
				}, 0);
				
				break;
			}
		}
	}, true);
	
	var stepper = this.createStepper(input, inputUpdate, 1, 10, true, Menus.prototype.defaultFontSize);
	stepper.style.display = input.style.display;
	stepper.style.marginTop = '4px';
	
	if (!mxClient.IS_QUIRKS)
	{
		stepper.style.right = '20px';
	}
	
	stylePanel2.appendChild(stepper);
	
	var arrow = fontMenu.getElementsByTagName('div')[0];
	arrow.style.cssFloat = 'right';
	
	var bgColorApply = null;
	var currentBgColor = '#ffffff';
	
	var fontColorApply = null;
	var currentFontColor = '#000000';
		
	var bgPanel = (graph.cellEditor.isContentEditing()) ? this.createColorOption(mxResources.get('backgroundColor'), function()
	{
		return currentBgColor;
	}, function(color)
	{
		document.execCommand('backcolor', false, (color != mxConstants.NONE) ? color : 'transparent');
	}, '#ffffff',
	{
		install: function(apply) { bgColorApply = apply; },
		destroy: function() { bgColorApply = null; }
	}, null, true) : this.createCellColorOption(mxResources.get('backgroundColor'), mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, '#ffffff');
	bgPanel.style.fontWeight = 'bold';

	var borderPanel = this.createCellColorOption(mxResources.get('borderColor'), mxConstants.STYLE_LABEL_BORDERCOLOR, '#000000');
	borderPanel.style.fontWeight = 'bold';
	
	var panel = (graph.cellEditor.isContentEditing()) ? this.createColorOption(mxResources.get('fontColor'), function()
	{
		return currentFontColor;
	}, function(color)
	{
		document.execCommand('forecolor', false, (color != mxConstants.NONE) ? color : 'transparent');
	}, '#000000',
	{
		install: function(apply) { fontColorApply = apply; },
		destroy: function() { fontColorApply = null; }
	}, null, true) : this.createCellColorOption(mxResources.get('fontColor'), mxConstants.STYLE_FONTCOLOR, '#000000', function(color)
	{
		if (color == null || color == mxConstants.NONE)
		{
			bgPanel.style.display = 'none';
		}
		else
		{
			bgPanel.style.display = '';
		}
		
		borderPanel.style.display = bgPanel.style.display;
	}, function(color)
	{
		if (color == null || color == mxConstants.NONE)
		{
			graph.setCellStyles(mxConstants.STYLE_NOLABEL, '1', graph.getSelectionCells());
		}
		else
		{
			graph.setCellStyles(mxConstants.STYLE_NOLABEL, null, graph.getSelectionCells());
		}
	});
	panel.style.fontWeight = 'bold';
	
	colorPanel.appendChild(panel);
	colorPanel.appendChild(bgPanel);
	
	if (!graph.cellEditor.isContentEditing())
	{
		colorPanel.appendChild(borderPanel);
	}
	
	container.appendChild(colorPanel);

	var extraPanel = this.createPanel();
	extraPanel.style.paddingTop = '2px';
	extraPanel.style.paddingBottom = '4px';
	
	// LATER: Fix toggle using '' instead of 'null'
	var wwOpt = this.createCellOption(mxResources.get('wordWrap'), mxConstants.STYLE_WHITE_SPACE, null, 'wrap', 'null', null, null, true);
	wwOpt.style.fontWeight = 'bold';
	
	// Word wrap in edge labels only supported via labelWidth style
	if (!ss.containsLabel && !ss.autoSize && ss.edges.length == 0)
	{
		extraPanel.appendChild(wwOpt);
	}
	
	// Delegates switch of style to formattedText action as it also convertes newlines
	var htmlOpt = this.createCellOption(mxResources.get('formattedText'), 'html', '0',
		null, null, null, ui.actions.get('formattedText'));
	htmlOpt.style.fontWeight = 'bold';
	extraPanel.appendChild(htmlOpt);
	
	var spacingPanel = this.createPanel();
	spacingPanel.style.paddingTop = '10px';
	spacingPanel.style.paddingBottom = '28px';
	spacingPanel.style.fontWeight = 'normal';
	
	var span = document.createElement('div');
	span.style.position = 'absolute';
	span.style.width = '70px';
	span.style.marginTop = '0px';
	span.style.fontWeight = 'bold';
	mxUtils.write(span, mxResources.get('spacing'));
	spacingPanel.appendChild(span);

	var topUpdate, globalUpdate, leftUpdate, bottomUpdate, rightUpdate;
	var topSpacing = this.addUnitInput(spacingPanel, 'pt', 91, 44, function()
	{
		topUpdate.apply(this, arguments);
	});
	var globalSpacing = this.addUnitInput(spacingPanel, 'pt', 20, 44, function()
	{
		globalUpdate.apply(this, arguments);
	});

	mxUtils.br(spacingPanel);
	this.addLabel(spacingPanel, mxResources.get('top'), 91);
	this.addLabel(spacingPanel, mxResources.get('global'), 20);
	mxUtils.br(spacingPanel);
	mxUtils.br(spacingPanel);

	var leftSpacing = this.addUnitInput(spacingPanel, 'pt', 162, 44, function()
	{
		leftUpdate.apply(this, arguments);
	});
	var bottomSpacing = this.addUnitInput(spacingPanel, 'pt', 91, 44, function()
	{
		bottomUpdate.apply(this, arguments);
	});
	var rightSpacing = this.addUnitInput(spacingPanel, 'pt', 20, 44, function()
	{
		rightUpdate.apply(this, arguments);
	});

	mxUtils.br(spacingPanel);
	this.addLabel(spacingPanel, mxResources.get('left'), 162);
	this.addLabel(spacingPanel, mxResources.get('bottom'), 91);
	this.addLabel(spacingPanel, mxResources.get('right'), 20);
	
	if (!graph.cellEditor.isContentEditing())
	{
		container.appendChild(extraPanel);
		container.appendChild(this.createRelativeOption(mxResources.get('opacity'), mxConstants.STYLE_TEXT_OPACITY));
		container.appendChild(spacingPanel);
	}
	else
	{
		var selState = null;
		var lineHeightInput = null;
		
		container.appendChild(this.createRelativeOption(mxResources.get('lineheight'), null, null, function(input)
		{
			var value = (input.value == '') ? 120 : parseInt(input.value);
			value = Math.max(120, (isNaN(value)) ? 120 : value);

			if (selState != null)
			{
				graph.cellEditor.restoreSelection(selState);
				selState = null;
			}
			
			var selectedElement = graph.getSelectedElement();
			var node = selectedElement;
			
			while (node != null && node.nodeType != mxConstants.NODETYPE_ELEMENT)
			{
				node = node.parentNode;
			}
			
			if (node != null && node == graph.cellEditor.textarea && graph.cellEditor.textarea.firstChild != null)
			{
				if (graph.cellEditor.textarea.firstChild.nodeName != 'FONT')
				{
					graph.cellEditor.textarea.innerHTML = '<font>' + graph.cellEditor.textarea.innerHTML + '</font>';
				}
				
				node = graph.cellEditor.textarea.firstChild;
			}
			
			if (node != null && node != graph.cellEditor.textarea)
			{
				node.style.lineHeight = value + '%';
			}
			
			input.value = value + ' %';
		}, function(input)
		{
			// Used in CSS handler to update current value
			lineHeightInput = input;
			
			// KNOWN: Arrow up/down clear selection text in quirks/IE 8
			// Text size via arrow button limits to 16 in IE11. Why?
			mxEvent.addListener(input, 'mousedown', function()
			{
				selState = graph.cellEditor.saveSelection();
			});
			
			mxEvent.addListener(input, 'touchstart', function()
			{
				selState = graph.cellEditor.saveSelection();
			});
			
			input.value = '120 %';
		}));
		
		var insertPanel = stylePanel.cloneNode(false);
		insertPanel.style.paddingLeft = '0px';
		var insertBtns = this.editorUi.toolbar.addItems(['link', 'image'], insertPanel, true);

		var btns = [
		        this.editorUi.toolbar.addButton('geSprite-horizontalrule', mxResources.get('insertHorizontalRule'),
				function()
				{
					document.execCommand('inserthorizontalrule', false, null);
				}, insertPanel),				
				this.editorUi.toolbar.addMenuFunctionInContainer(insertPanel, 'geSprite-table', mxResources.get('table'), false, mxUtils.bind(this, function(menu)
				{
					this.editorUi.menus.addInsertTableItem(menu);
				}))];
		this.styleButtons(insertBtns);
		this.styleButtons(btns);
		
		var wrapper2 = this.createPanel();
		wrapper2.style.paddingTop = '10px';
		wrapper2.style.paddingBottom = '10px';
		wrapper2.appendChild(this.createTitle(mxResources.get('insert')));
		wrapper2.appendChild(insertPanel);
		container.appendChild(wrapper2);
		
		if (mxClient.IS_QUIRKS)
		{
			wrapper2.style.height = '70';
		}
		
		var tablePanel = stylePanel.cloneNode(false);
		tablePanel.style.paddingLeft = '0px';
		
		var btns = [
		        this.editorUi.toolbar.addButton('geSprite-insertcolumnbefore', mxResources.get('insertColumnBefore'),
				function()
				{
					try
					{
			        	if (currentTable != null)
			        	{
			        		graph.selectNode(graph.insertColumn(currentTable, (tableCell != null) ? tableCell.cellIndex : 0));
			        	}
					}
					catch (e)
					{
						alert(e);
					}
				}, tablePanel),
				this.editorUi.toolbar.addButton('geSprite-insertcolumnafter', mxResources.get('insertColumnAfter'),
				function()
				{
					try
					{
						if (currentTable != null)
			        	{
							graph.selectNode(graph.insertColumn(currentTable, (tableCell != null) ? tableCell.cellIndex + 1 : -1));
			        	}
					}
					catch (e)
					{
						alert(e);
					}
				}, tablePanel),
				this.editorUi.toolbar.addButton('geSprite-deletecolumn', mxResources.get('deleteColumn'),
				function()
				{
					try
					{
						if (currentTable != null && tableCell != null)
						{
							graph.deleteColumn(currentTable, tableCell.cellIndex);
						}
					}
					catch (e)
					{
						alert(e);
					}
				}, tablePanel),
				this.editorUi.toolbar.addButton('geSprite-insertrowbefore', mxResources.get('insertRowBefore'),
				function()
				{
					try
					{
						if (currentTable != null && tableRow != null)
						{
							graph.selectNode(graph.insertRow(currentTable, tableRow.sectionRowIndex));
						}
					}
					catch (e)
					{
						alert(e);
					}
				}, tablePanel),
				this.editorUi.toolbar.addButton('geSprite-insertrowafter', mxResources.get('insertRowAfter'),
				function()
				{
					try
					{
						if (currentTable != null && tableRow != null)
						{
							graph.selectNode(graph.insertRow(currentTable, tableRow.sectionRowIndex + 1));
						}
					}
					catch (e)
					{
						alert(e);
					}
				}, tablePanel),
				this.editorUi.toolbar.addButton('geSprite-deleterow', mxResources.get('deleteRow'),
				function()
				{
					try
					{
						if (currentTable != null && tableRow != null)
						{
							graph.deleteRow(currentTable, tableRow.sectionRowIndex);
						}
					}
					catch (e)
					{
						alert(e);
					}
				}, tablePanel)];
		this.styleButtons(btns);
		btns[2].style.marginRight = '9px';
		
		var wrapper3 = this.createPanel();
		wrapper3.style.paddingTop = '10px';
		wrapper3.style.paddingBottom = '10px';
		wrapper3.appendChild(this.createTitle(mxResources.get('table')));
		wrapper3.appendChild(tablePanel);

		if (mxClient.IS_QUIRKS)
		{
			mxUtils.br(container);
			wrapper3.style.height = '70';
		}
		
		var tablePanel2 = stylePanel.cloneNode(false);
		tablePanel2.style.paddingLeft = '0px';
		
		var btns = [
		        this.editorUi.toolbar.addButton('geSprite-strokecolor', mxResources.get('borderColor'),
				mxUtils.bind(this, function()
				{
					if (currentTable != null)
					{
						// Converts rgb(r,g,b) values
						var color = currentTable.style.borderColor.replace(
							    /\brgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)/g,
							    function($0, $1, $2, $3) {
							        return "#" + ("0"+Number($1).toString(16)).substr(-2) + ("0"+Number($2).toString(16)).substr(-2) + ("0"+Number($3).toString(16)).substr(-2);
							    });
						this.editorUi.pickColor(color, function(newColor)
						{
							if (newColor == null || newColor == mxConstants.NONE)
							{
								currentTable.removeAttribute('border');
								currentTable.style.border = '';
								currentTable.style.borderCollapse = '';
							}
							else
							{
								currentTable.setAttribute('border', '1');
								currentTable.style.border = '1px solid ' + newColor;
								currentTable.style.borderCollapse = 'collapse';
							}
						});
					}
				}), tablePanel2),
				this.editorUi.toolbar.addButton('geSprite-fillcolor', mxResources.get('backgroundColor'),
				mxUtils.bind(this, function()
				{
					// Converts rgb(r,g,b) values
					if (currentTable != null)
					{
						var color = currentTable.style.backgroundColor.replace(
							    /\brgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)/g,
							    function($0, $1, $2, $3) {
							        return "#" + ("0"+Number($1).toString(16)).substr(-2) + ("0"+Number($2).toString(16)).substr(-2) + ("0"+Number($3).toString(16)).substr(-2);
							    });
						this.editorUi.pickColor(color, function(newColor)
						{
							if (newColor == null || newColor == mxConstants.NONE)
							{
								currentTable.style.backgroundColor = '';
							}
							else
							{
								currentTable.style.backgroundColor = newColor;
							}
						});
					}
				}), tablePanel2),
				this.editorUi.toolbar.addButton('geSprite-fit', mxResources.get('spacing'),
				function()
				{
					if (currentTable != null)
					{
						var value = currentTable.getAttribute('cellPadding') || 0;
						
						var dlg = new FilenameDialog(ui, value, mxResources.get('apply'), mxUtils.bind(this, function(newValue)
						{
							if (newValue != null && newValue.length > 0)
							{
								currentTable.setAttribute('cellPadding', newValue);
							}
							else
							{
								currentTable.removeAttribute('cellPadding');
							}
						}), mxResources.get('spacing'));
						ui.showDialog(dlg.container, 300, 80, true, true);
						dlg.init();
					}
				}, tablePanel2),
				this.editorUi.toolbar.addButton('geSprite-left', mxResources.get('left'),
				function()
				{
					if (currentTable != null)
					{
						currentTable.setAttribute('align', 'left');
					}
				}, tablePanel2),
				this.editorUi.toolbar.addButton('geSprite-center', mxResources.get('center'),
				function()
				{
					if (currentTable != null)
					{
						currentTable.setAttribute('align', 'center');
					}
				}, tablePanel2),
				this.editorUi.toolbar.addButton('geSprite-right', mxResources.get('right'),
				function()
				{
					if (currentTable != null)
					{
						currentTable.setAttribute('align', 'right');
					}
				}, tablePanel2)];
		this.styleButtons(btns);
		btns[2].style.marginRight = '9px';
		
		if (mxClient.IS_QUIRKS)
		{
			mxUtils.br(wrapper3);
			mxUtils.br(wrapper3);
		}
		
		wrapper3.appendChild(tablePanel2);
		container.appendChild(wrapper3);
		
		tableWrapper = wrapper3;
	}
	
	function setSelected(elt, selected)
	{
		if (mxClient.IS_IE && (mxClient.IS_QUIRKS || document.documentMode < 10))
		{
			elt.style.filter = (selected) ? 'progid:DXImageTransform.Microsoft.Gradient('+
            	'StartColorStr=\'#c5ecff\', EndColorStr=\'#87d4fb\', GradientType=0)' : '';
		}
		else
		{
			elt.style.backgroundImage = (selected) ? 'linear-gradient(#c5ecff 0px,#87d4fb 100%)' : '';
		}
	};
	
	var listener = mxUtils.bind(this, function(sender, evt, force)
	{
		ss = this.format.getSelectionState();
		var fontStyle = mxUtils.getValue(ss.style, mxConstants.STYLE_FONTSTYLE, 0);
		setSelected(fontStyleItems[0], (fontStyle & mxConstants.FONT_BOLD) == mxConstants.FONT_BOLD);
		setSelected(fontStyleItems[1], (fontStyle & mxConstants.FONT_ITALIC) == mxConstants.FONT_ITALIC);
		setSelected(fontStyleItems[2], (fontStyle & mxConstants.FONT_UNDERLINE) == mxConstants.FONT_UNDERLINE);
		fontMenu.firstChild.nodeValue = mxUtils.htmlEntities(mxUtils.getValue(ss.style, mxConstants.STYLE_FONTFAMILY, Menus.prototype.defaultFont));

		setSelected(verticalItem, mxUtils.getValue(ss.style, mxConstants.STYLE_HORIZONTAL, '1') == '0');
		
		if (force || document.activeElement != input)
		{
			var tmp = parseFloat(mxUtils.getValue(ss.style, mxConstants.STYLE_FONTSIZE, Menus.prototype.defaultFontSize));
			input.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
		
		var align = mxUtils.getValue(ss.style, mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
		setSelected(left, align == mxConstants.ALIGN_LEFT);
		setSelected(center, align == mxConstants.ALIGN_CENTER);
		setSelected(right, align == mxConstants.ALIGN_RIGHT);
		
		var valign = mxUtils.getValue(ss.style, mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		setSelected(top, valign == mxConstants.ALIGN_TOP);
		setSelected(middle, valign == mxConstants.ALIGN_MIDDLE);
		setSelected(bottom, valign == mxConstants.ALIGN_BOTTOM);
		
		var pos = mxUtils.getValue(ss.style, mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
		var vpos =  mxUtils.getValue(ss.style, mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
		
		if (pos == mxConstants.ALIGN_LEFT && vpos == mxConstants.ALIGN_TOP)
		{
			positionSelect.value = 'topLeft';
		}
		else if (pos == mxConstants.ALIGN_CENTER && vpos == mxConstants.ALIGN_TOP)
		{
			positionSelect.value = 'top';
		}
		else if (pos == mxConstants.ALIGN_RIGHT && vpos == mxConstants.ALIGN_TOP)
		{
			positionSelect.value = 'topRight';
		}
		else if (pos == mxConstants.ALIGN_LEFT && vpos == mxConstants.ALIGN_BOTTOM)
		{
			positionSelect.value = 'bottomLeft';
		}
		else if (pos == mxConstants.ALIGN_CENTER && vpos == mxConstants.ALIGN_BOTTOM)
		{
			positionSelect.value = 'bottom';
		}
		else if (pos == mxConstants.ALIGN_RIGHT && vpos == mxConstants.ALIGN_BOTTOM)
		{
			positionSelect.value = 'bottomRight';
		}
		else if (pos == mxConstants.ALIGN_LEFT)
		{
			positionSelect.value = 'left';
		}
		else if (pos == mxConstants.ALIGN_RIGHT)
		{
			positionSelect.value = 'right';
		}
		else
		{
			positionSelect.value = 'center';
		}
		
		var dir = mxUtils.getValue(ss.style, mxConstants.STYLE_TEXT_DIRECTION, mxConstants.DEFAULT_TEXT_DIRECTION);
		
		if (dir == mxConstants.TEXT_DIRECTION_RTL)
		{
			dirSelect.value = 'rightToLeft';
		}
		else if (dir == mxConstants.TEXT_DIRECTION_LTR)
		{
			dirSelect.value = 'leftToRight';
		}
		else if (dir == mxConstants.TEXT_DIRECTION_AUTO)
		{
			dirSelect.value = 'automatic';
		}
		
		if (force || document.activeElement != globalSpacing)
		{
			var tmp = parseFloat(mxUtils.getValue(ss.style, mxConstants.STYLE_SPACING, 2));
			globalSpacing.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}

		if (force || document.activeElement != topSpacing)
		{
			var tmp = parseFloat(mxUtils.getValue(ss.style, mxConstants.STYLE_SPACING_TOP, 0));
			topSpacing.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
		
		if (force || document.activeElement != rightSpacing)
		{
			var tmp = parseFloat(mxUtils.getValue(ss.style, mxConstants.STYLE_SPACING_RIGHT, 0));
			rightSpacing.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
		
		if (force || document.activeElement != bottomSpacing)
		{
			var tmp = parseFloat(mxUtils.getValue(ss.style, mxConstants.STYLE_SPACING_BOTTOM, 0));
			bottomSpacing.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
		
		if (force || document.activeElement != leftSpacing)
		{
			var tmp = parseFloat(mxUtils.getValue(ss.style, mxConstants.STYLE_SPACING_LEFT, 0));
			leftSpacing.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
	});

	globalUpdate = this.installInputHandler(globalSpacing, mxConstants.STYLE_SPACING, 2, -999, 999, ' pt');
	topUpdate = this.installInputHandler(topSpacing, mxConstants.STYLE_SPACING_TOP, 0, -999, 999, ' pt');
	rightUpdate = this.installInputHandler(rightSpacing, mxConstants.STYLE_SPACING_RIGHT, 0, -999, 999, ' pt');
	bottomUpdate = this.installInputHandler(bottomSpacing, mxConstants.STYLE_SPACING_BOTTOM, 0, -999, 999, ' pt');
	leftUpdate = this.installInputHandler(leftSpacing, mxConstants.STYLE_SPACING_LEFT, 0, -999, 999, ' pt');

	this.addKeyHandler(input, listener);
	this.addKeyHandler(globalSpacing, listener);
	this.addKeyHandler(topSpacing, listener);
	this.addKeyHandler(rightSpacing, listener);
	this.addKeyHandler(bottomSpacing, listener);
	this.addKeyHandler(leftSpacing, listener);

	graph.getModel().addListener(mxEvent.CHANGE, listener);
	this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
	listener();
	
	if (graph.cellEditor.isContentEditing())
	{
		var updating = false;
		
		var updateCssHandler = function()
		{
			if (!updating)
			{
				updating = true;
			
				window.setTimeout(function()
				{
					var selectedElement = graph.getSelectedElement();
					var node = selectedElement;
					
					while (node != null && node.nodeType != mxConstants.NODETYPE_ELEMENT)
					{
						node = node.parentNode;
					}
					
					if (node != null)
					{
						var css = mxUtils.getCurrentStyle(node);
						
						if (css != null)
						{
							setSelected(fontStyleItems[0], css.fontWeight == 'bold' || graph.getParentByName(node, 'B', graph.cellEditor.textarea) != null);
							setSelected(fontStyleItems[1], css.fontStyle == 'italic' || graph.getParentByName(node, 'I', graph.cellEditor.textarea) != null);
							setSelected(fontStyleItems[2], graph.getParentByName(node, 'U', graph.cellEditor.textarea) != null);
							setSelected(left, css.textAlign == 'left');
							setSelected(center, css.textAlign == 'center');
							setSelected(right, css.textAlign == 'right');
							setSelected(full, css.textAlign == 'justify');
							setSelected(sup, graph.getParentByName(node, 'SUP', graph.cellEditor.textarea) != null);
							setSelected(sub, graph.getParentByName(node, 'SUB', graph.cellEditor.textarea) != null);
							
							currentTable = graph.getParentByName(node, 'TABLE', graph.cellEditor.textarea);
							tableRow = (currentTable == null) ? null : graph.getParentByName(node, 'TR', currentTable);
							tableCell = (currentTable == null) ? null : graph.getParentByName(node, 'TD', currentTable);
							tableWrapper.style.display = (currentTable != null) ? '' : 'none';
							
							if (document.activeElement != input)
							{
								if (node.nodeName == 'FONT' && node.getAttribute('size') == '4' &&
									pendingFontSize != null)
								{
									node.removeAttribute('size');
									node.style.fontSize = pendingFontSize + 'px';
									pendingFontSize = null;
								}
								else
								{
									input.value = parseFloat(css.fontSize) + ' pt';
								}
								
								var tmp = node.style.lineHeight || css.lineHeight;
								var lh = parseFloat(tmp);
								
								if (tmp.substring(tmp.length - 2) == 'px')
								{
									lh = lh / parseFloat(css.fontSize);
								}
								
								if (tmp.substring(tmp.length - 1) != '%')
								{
									lh *= 100; 
								}
								
								lineHeightInput.value = lh + ' %';
							}
							
							// Converts rgb(r,g,b) values
							var color = css.color.replace(
								    /\brgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)/g,
								    function($0, $1, $2, $3) {
								        return "#" + ("0"+Number($1).toString(16)).substr(-2) + ("0"+Number($2).toString(16)).substr(-2) + ("0"+Number($3).toString(16)).substr(-2);
								    });
							var color2 = css.backgroundColor.replace(
								    /\brgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)/g,
								    function($0, $1, $2, $3) {
								        return "#" + ("0"+Number($1).toString(16)).substr(-2) + ("0"+Number($2).toString(16)).substr(-2) + ("0"+Number($3).toString(16)).substr(-2);
								    });
							
							// Updates the color picker for the current font
							if (fontColorApply != null)
							{
								if (color.charAt(0) == '#')
								{
									currentFontColor = color;
								}
								else
								{
									currentFontColor = '#000000';
								}
								
								fontColorApply(currentFontColor, true);
							}
							
							if (bgColorApply != null)
							{
								if (color2.charAt(0) == '#')
								{
									currentBgColor = color2;
								}
								else
								{
									currentBgColor = null;
								}
								
								bgColorApply(currentBgColor, true);
							}
							
							// Workaround for firstChild is null or not an object
							// in the log which seems to be IE8- only / 29.01.15
							if (fontMenu.firstChild != null)
							{
								// Strips leading and trailing quotes
								var ff = css.fontFamily;
								
								if (ff.charAt(0) == '\'')
								{
									ff = ff.substring(1);
								}
								
								if (ff.charAt(ff.length - 1) == '\'')
								{
									ff = ff.substring(0, ff.length - 1);
								}
								
								fontMenu.firstChild.nodeValue = ff;
							}
						}
					}
					
					updating = false;
				}, 0);
			}
		};
		
		mxEvent.addListener(graph.cellEditor.textarea, 'input', updateCssHandler)
		mxEvent.addListener(graph.cellEditor.textarea, 'touchend', updateCssHandler);
		mxEvent.addListener(graph.cellEditor.textarea, 'mouseup', updateCssHandler);
		mxEvent.addListener(graph.cellEditor.textarea, 'keyup', updateCssHandler);
		this.listeners.push({destroy: function()
		{
			// No need to remove listener since textarea is destroyed after edit
		}});
		updateCssHandler();
	}

	return container;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
StyleFormatPanel = function(format, editorUi, container)
{
	BaseFormatPanel.call(this, format, editorUi, container);
	this.init();
};

mxUtils.extend(StyleFormatPanel, BaseFormatPanel);

/**
 * Adds the label menu items to the given menu and parent.
 */
StyleFormatPanel.prototype.init = function()
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	var ss = this.format.getSelectionState();
	
	if (!ss.containsImage || ss.style.shape == 'image')
	{
		this.container.appendChild(this.addFill(this.createPanel()));
	}

	this.container.appendChild(this.addStroke(this.createPanel()));
	var opacityPanel = this.createRelativeOption(mxResources.get('opacity'), mxConstants.STYLE_OPACITY, 41);
	opacityPanel.style.paddingTop = '8px';
	opacityPanel.style.paddingBottom = '8px';
	this.container.appendChild(opacityPanel);
	this.container.appendChild(this.addEffects(this.createPanel()));
	var opsPanel = this.addEditOps(this.createPanel());
	
	if (opsPanel.firstChild != null)
	{
		mxUtils.br(opsPanel);
	}
	
	this.container.appendChild(this.addStyleOps(opsPanel));
};

/**
 * Adds the label menu items to the given menu and parent.
 */
StyleFormatPanel.prototype.addEditOps = function(div)
{
	var ss = this.format.getSelectionState();
	var btn = null;
	
	if (this.editorUi.editor.graph.getSelectionCount() == 1)
	{
		btn = mxUtils.button(mxResources.get('editStyle'), mxUtils.bind(this, function(evt)
		{
			this.editorUi.actions.get('editStyle').funct();
		}));
		
		btn.setAttribute('title', mxResources.get('editStyle') + ' (' + this.editorUi.actions.get('editStyle').shortcut + ')');
		btn.style.width = '202px';
		btn.style.marginBottom = '2px';
		
		div.appendChild(btn);
	}
	
	if (ss.image)
	{
		var btn2 = mxUtils.button(mxResources.get('editImage'), mxUtils.bind(this, function(evt)
		{
			this.editorUi.actions.get('image').funct();
		}));
		
		btn2.setAttribute('title', mxResources.get('editImage'));
		btn2.style.marginBottom = '2px';
		
		if (btn == null)
		{
			btn2.style.width = '202px';
		}
		else
		{
			btn.style.width = '100px';
			btn2.style.width = '100px';
			btn2.style.marginLeft = '2px';
		}
		
		div.appendChild(btn2);
	}
	
	return div;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
StyleFormatPanel.prototype.addFill = function(container)
{
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	var ss = this.format.getSelectionState();
	container.style.paddingTop = '6px';
	container.style.paddingBottom = '6px';

	// Adds gradient direction option
	var gradientSelect = document.createElement('select');
	gradientSelect.style.position = 'absolute';
	gradientSelect.style.marginTop = '-2px';
	gradientSelect.style.right = (mxClient.IS_QUIRKS) ? '52px' : '72px';
	gradientSelect.style.width = '70px';
	
	// Stops events from bubbling to color option event handler
	mxEvent.addListener(gradientSelect, 'click', function(evt)
	{
		mxEvent.consume(evt);
	});

	var gradientPanel = this.createCellColorOption(mxResources.get('gradient'), mxConstants.STYLE_GRADIENTCOLOR, '#ffffff', function(color)
	{
		if (color == null || color == mxConstants.NONE)
		{
			gradientSelect.style.display = 'none';
		}
		else
		{
			gradientSelect.style.display = '';
		}
	});

	var fillKey = (ss.style.shape == 'image') ? mxConstants.STYLE_IMAGE_BACKGROUND : mxConstants.STYLE_FILLCOLOR;
	
	var fillPanel = this.createCellColorOption(mxResources.get('fill'), fillKey, '#ffffff');
	fillPanel.style.fontWeight = 'bold';

	var tmpColor = mxUtils.getValue(ss.style, fillKey, null);
	gradientPanel.style.display = (tmpColor != null && tmpColor != mxConstants.NONE &&
		ss.fill && ss.style.shape != 'image') ? '' : 'none';

	var directions = [mxConstants.DIRECTION_NORTH, mxConstants.DIRECTION_EAST,
	                  mxConstants.DIRECTION_SOUTH, mxConstants.DIRECTION_WEST];

	for (var i = 0; i < directions.length; i++)
	{
		var gradientOption = document.createElement('option');
		gradientOption.setAttribute('value', directions[i]);
		mxUtils.write(gradientOption, mxResources.get(directions[i]));
		gradientSelect.appendChild(gradientOption);
	}
	
	gradientPanel.appendChild(gradientSelect);

	var listener = mxUtils.bind(this, function()
	{
		ss = this.format.getSelectionState();
		var value = mxUtils.getValue(ss.style, mxConstants.STYLE_GRADIENT_DIRECTION, mxConstants.DIRECTION_SOUTH);
		
		// Handles empty string which is not allowed as a value
		if (value == '')
		{
			value = mxConstants.DIRECTION_SOUTH;
		}
		
		gradientSelect.value = value;
		container.style.display = (ss.fill) ? '' : 'none';
		
		var fillColor = mxUtils.getValue(ss.style, mxConstants.STYLE_FILLCOLOR, null);

		if (!ss.fill || ss.containsImage || fillColor == null || fillColor == mxConstants.NONE)
		{
			gradientPanel.style.display = 'none';
		}
		else
		{
			gradientPanel.style.display = '';
		}
	});
	
	graph.getModel().addListener(mxEvent.CHANGE, listener);
	this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
	listener();

	mxEvent.addListener(gradientSelect, 'change', function(evt)
	{
		graph.setCellStyles(mxConstants.STYLE_GRADIENT_DIRECTION, gradientSelect.value, graph.getSelectionCells());
		mxEvent.consume(evt);
	});
	
	container.appendChild(fillPanel);
	container.appendChild(gradientPanel);

	if (ss.style.shape == 'swimlane')
	{
		container.appendChild(this.createCellColorOption(mxResources.get('laneColor'), 'swimlaneFillColor', '#ffffff'));
	}

	return container;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
StyleFormatPanel.prototype.addStroke = function(container)
{
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	var ss = this.format.getSelectionState();
	
	container.style.paddingTop = '4px';
	container.style.paddingBottom = '4px';
	container.style.whiteSpace = 'normal';
	
	var colorPanel = document.createElement('div');
	colorPanel.style.fontWeight = 'bold';
	
	// Adds gradient direction option
	var styleSelect = document.createElement('select');
	styleSelect.style.position = 'absolute';
	styleSelect.style.marginTop = '-2px';
	styleSelect.style.right = '72px';
	styleSelect.style.width = '80px';

	var styles = ['sharp', 'rounded', 'curved'];

	for (var i = 0; i < styles.length; i++)
	{
		var styleOption = document.createElement('option');
		styleOption.setAttribute('value', styles[i]);
		mxUtils.write(styleOption, mxResources.get(styles[i]));
		styleSelect.appendChild(styleOption);
	}
	
	mxEvent.addListener(styleSelect, 'change', function(evt)
	{
		graph.getModel().beginUpdate();
		try
		{
			var keys = [mxConstants.STYLE_ROUNDED, mxConstants.STYLE_CURVED];
			// Default for rounded is 1
			var values = ['0', null];
			
			if (styleSelect.value == 'rounded')
			{
				values = ['1', null];
			}
			else if (styleSelect.value == 'curved')
			{
				values = [null, '1'];
			}
			
			for (var i = 0; i < keys.length; i++)
			{
				graph.setCellStyles(keys[i], values[i], graph.getSelectionCells());
			}
			
			ui.fireEvent(new mxEventObject('styleChanged', 'keys', keys,
				'values', values, 'cells', graph.getSelectionCells()));
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		mxEvent.consume(evt);
	});
	
	// Stops events from bubbling to color option event handler
	mxEvent.addListener(styleSelect, 'click', function(evt)
	{
		mxEvent.consume(evt);
	});

	var strokeKey = (ss.style.shape == 'image') ? mxConstants.STYLE_IMAGE_BORDER : mxConstants.STYLE_STROKECOLOR;
	
	var lineColor = this.createCellColorOption(mxResources.get('line'), strokeKey, '#000000');
	lineColor.appendChild(styleSelect);
	colorPanel.appendChild(lineColor);
	
	// Used if only edges selected
	var stylePanel = colorPanel.cloneNode(false);
	stylePanel.style.fontWeight = 'normal';
	stylePanel.style.whiteSpace = 'nowrap';
	stylePanel.style.position = 'relative';
	stylePanel.style.paddingLeft = '16px'
	stylePanel.style.marginBottom = '2px';
	stylePanel.style.marginTop = '2px';
	stylePanel.className = 'geToolbarContainer';

	var addItem = mxUtils.bind(this, function(menu, width, cssName, keys, values)
	{
		var item = this.editorUi.menus.styleChange(menu, '', keys, values, 'geIcon', null);
	
		var pat = document.createElement('div');
		pat.style.width = width + 'px';
		pat.style.height = '1px';
		pat.style.borderBottom = '1px ' + cssName + ' black';
		pat.style.paddingTop = '6px';

		item.firstChild.firstChild.style.padding = '0px 4px 0px 4px';
		item.firstChild.firstChild.style.width = width + 'px';
		item.firstChild.firstChild.appendChild(pat);
		
		return item;
	});

	var pattern = this.editorUi.toolbar.addMenuFunctionInContainer(stylePanel, 'geSprite-orthogonal', mxResources.get('pattern'), false, mxUtils.bind(this, function(menu)
	{
		addItem(menu, 75, 'solid', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], [null, null]).setAttribute('title', mxResources.get('solid'));
		addItem(menu, 75, 'dashed', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], ['1', null]).setAttribute('title', mxResources.get('dashed'));
		addItem(menu, 75, 'dotted', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], ['1', '1 1']).setAttribute('title', mxResources.get('dotted') + ' (1)');
		addItem(menu, 75, 'dotted', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], ['1', '1 2']).setAttribute('title', mxResources.get('dotted') + ' (2)');
		addItem(menu, 75, 'dotted', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], ['1', '1 4']).setAttribute('title', mxResources.get('dotted') + ' (3)');
	}));
	
	// Used for mixed selection (vertices and edges)
	var altStylePanel = stylePanel.cloneNode(false);
	
	var edgeShape = this.editorUi.toolbar.addMenuFunctionInContainer(altStylePanel, 'geSprite-connection', mxResources.get('connection'), false, mxUtils.bind(this, function(menu)
	{
		this.editorUi.menus.styleChange(menu, '', [mxConstants.STYLE_SHAPE, mxConstants.STYLE_STARTSIZE, mxConstants.STYLE_ENDSIZE, 'width'], [null, null, null, null], 'geIcon geSprite geSprite-connection', null, true).setAttribute('title', mxResources.get('line'));
		this.editorUi.menus.styleChange(menu, '', [mxConstants.STYLE_SHAPE, mxConstants.STYLE_STARTSIZE, mxConstants.STYLE_ENDSIZE, 'width'], ['link', null, null, null], 'geIcon geSprite geSprite-linkedge', null, true).setAttribute('title', mxResources.get('link'));
		this.editorUi.menus.styleChange(menu, '', [mxConstants.STYLE_SHAPE, mxConstants.STYLE_STARTSIZE, mxConstants.STYLE_ENDSIZE, 'width'], ['flexArrow', null, null, null], 'geIcon geSprite geSprite-arrow', null, true).setAttribute('title', mxResources.get('arrow'));
		this.editorUi.menus.styleChange(menu, '', [mxConstants.STYLE_SHAPE, mxConstants.STYLE_STARTSIZE, mxConstants.STYLE_ENDSIZE, 'width'], ['arrow', null, null, null], 'geIcon geSprite geSprite-simplearrow', null, true).setAttribute('title', mxResources.get('simpleArrow')); 
	}));

	var altPattern = this.editorUi.toolbar.addMenuFunctionInContainer(altStylePanel, 'geSprite-orthogonal', mxResources.get('pattern'), false, mxUtils.bind(this, function(menu)
	{
		addItem(menu, 33, 'solid', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], [null, null]).setAttribute('title', mxResources.get('solid'));
		addItem(menu, 33, 'dashed', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], ['1', null]).setAttribute('title', mxResources.get('dashed'));
		addItem(menu, 33, 'dotted', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], ['1', '1 1']).setAttribute('title', mxResources.get('dotted') + ' (1)');
		addItem(menu, 33, 'dotted', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], ['1', '1 2']).setAttribute('title', mxResources.get('dotted') + ' (2)');
		addItem(menu, 33, 'dotted', [mxConstants.STYLE_DASHED, mxConstants.STYLE_DASH_PATTERN], ['1', '1 4']).setAttribute('title', mxResources.get('dotted') + ' (3)');
	}));
	
	var stylePanel2 = stylePanel.cloneNode(false);

	// Stroke width
	var input = document.createElement('input');
	input.style.textAlign = 'right';
	input.style.marginTop = '2px';
	input.style.width = '41px';
	input.setAttribute('title', mxResources.get('linewidth'));
	
	stylePanel.appendChild(input);
	
	var altInput = input.cloneNode(true);
	altStylePanel.appendChild(altInput);

	function update(evt)
	{
		// Maximum stroke width is 999
		var value = parseInt(input.value);
		value = Math.min(999, Math.max(1, (isNaN(value)) ? 1 : value));
		
		if (value != mxUtils.getValue(ss.style, mxConstants.STYLE_STROKEWIDTH, 1))
		{
			graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, value, graph.getSelectionCells());
			ui.fireEvent(new mxEventObject('styleChanged', 'keys', [mxConstants.STYLE_STROKEWIDTH],
					'values', [value], 'cells', graph.getSelectionCells()));
		}

		input.value = value + ' pt';
		mxEvent.consume(evt);
	};

	function altUpdate(evt)
	{
		// Maximum stroke width is 999
		var value = parseInt(altInput.value);
		value = Math.min(999, Math.max(1, (isNaN(value)) ? 1 : value));
		
		if (value != mxUtils.getValue(ss.style, mxConstants.STYLE_STROKEWIDTH, 1))
		{
			graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, value, graph.getSelectionCells());
			ui.fireEvent(new mxEventObject('styleChanged', 'keys', [mxConstants.STYLE_STROKEWIDTH],
					'values', [value], 'cells', graph.getSelectionCells()));
		}

		altInput.value = value + ' pt';
		mxEvent.consume(evt);
	};

	var stepper = this.createStepper(input, update, 1, 9);
	stepper.style.display = input.style.display;
	stepper.style.marginTop = '2px';
	stylePanel.appendChild(stepper);
	
	var altStepper = this.createStepper(altInput, altUpdate, 1, 9);
	altStepper.style.display = altInput.style.display;
	altStepper.style.marginTop = '2px';
	altStylePanel.appendChild(altStepper);
	
	if (!mxClient.IS_QUIRKS)
	{
		input.style.position = 'absolute';
		input.style.right = '32px';
		input.style.height = '15px';
		stepper.style.right = '20px';

		altInput.style.position = 'absolute';
		altInput.style.right = '32px';
		altInput.style.height = '15px';
		altStepper.style.right = '20px';
	}
	else
	{
		input.style.height = '17px';
		altInput.style.height = '17px';
	}
	
	mxEvent.addListener(input, 'blur', update);
	mxEvent.addListener(input, 'change', update);

	mxEvent.addListener(altInput, 'blur', altUpdate);
	mxEvent.addListener(altInput, 'change', altUpdate);
	
	if (mxClient.IS_QUIRKS)
	{
		mxUtils.br(stylePanel2);
		mxUtils.br(stylePanel2);
	}
	
	var edgeStyle = this.editorUi.toolbar.addMenuFunctionInContainer(stylePanel2, 'geSprite-orthogonal', mxResources.get('waypoints'), false, mxUtils.bind(this, function(menu)
	{
		if (ss.style.shape != 'arrow')
		{
			this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_EDGE, mxConstants.STYLE_CURVED, mxConstants.STYLE_NOEDGESTYLE], [null, null, null], 'geIcon geSprite geSprite-straight', null, true).setAttribute('title', mxResources.get('straight'));
			this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_EDGE, mxConstants.STYLE_CURVED, mxConstants.STYLE_NOEDGESTYLE], ['orthogonalEdgeStyle', null, null], 'geIcon geSprite geSprite-orthogonal', null, true).setAttribute('title', mxResources.get('orthogonal'));
			this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_EDGE, mxConstants.STYLE_ELBOW, mxConstants.STYLE_CURVED, mxConstants.STYLE_NOEDGESTYLE], ['elbowEdgeStyle', null, null, null], 'geIcon geSprite geSprite-horizontalelbow', null, true).setAttribute('title', mxResources.get('simple'));
			this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_EDGE, mxConstants.STYLE_ELBOW, mxConstants.STYLE_CURVED, mxConstants.STYLE_NOEDGESTYLE], ['elbowEdgeStyle', 'vertical', null, null], 'geIcon geSprite geSprite-verticalelbow', null, true).setAttribute('title', mxResources.get('simple'));
			this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_EDGE, mxConstants.STYLE_ELBOW, mxConstants.STYLE_CURVED, mxConstants.STYLE_NOEDGESTYLE], ['isometricEdgeStyle', null, null, null], 'geIcon geSprite geSprite-horizontalisometric', null, true).setAttribute('title', mxResources.get('isometric'));
			this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_EDGE, mxConstants.STYLE_ELBOW, mxConstants.STYLE_CURVED, mxConstants.STYLE_NOEDGESTYLE], ['isometricEdgeStyle', 'vertical', null, null], 'geIcon geSprite geSprite-verticalisometric', null, true).setAttribute('title', mxResources.get('isometric'));
	
			if (ss.style.shape == 'connector')
			{
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_EDGE, mxConstants.STYLE_CURVED, mxConstants.STYLE_NOEDGESTYLE], ['orthogonalEdgeStyle', '1', null], 'geIcon geSprite geSprite-curved', null, true).setAttribute('title', mxResources.get('curved'));
			}
			
			this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_EDGE, mxConstants.STYLE_CURVED, mxConstants.STYLE_NOEDGESTYLE], ['entityRelationEdgeStyle', null, null], 'geIcon geSprite geSprite-entity', null, true).setAttribute('title', mxResources.get('entityRelation'));
		}
	}));

	var lineStart = this.editorUi.toolbar.addMenuFunctionInContainer(stylePanel2, 'geSprite-startclassic', mxResources.get('linestart'), false, mxUtils.bind(this, function(menu)
	{
		if (ss.style.shape == 'connector' || ss.style.shape == 'flexArrow')
		{
			var item = this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.NONE, 0], 'geIcon', null, false);
			item.setAttribute('title', mxResources.get('none'));
			item.firstChild.firstChild.innerHTML = '<font style="font-size:10px;">' + mxUtils.htmlEntities(mxResources.get('none')) + '</font>';

			if (ss.style.shape == 'connector')
			{
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_CLASSIC, 1], 'geIcon geSprite geSprite-startclassic', null, false).setAttribute('title', mxResources.get('classic'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_CLASSIC_THIN, 1], 'geIcon geSprite geSprite-startclassicthin', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_OPEN, 0], 'geIcon geSprite geSprite-startopen', null, false).setAttribute('title', mxResources.get('openArrow'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_OPEN_THIN, 0], 'geIcon geSprite geSprite-startopenthin', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['openAsync', 0], 'geIcon geSprite geSprite-startopenasync', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_BLOCK, 1], 'geIcon geSprite geSprite-startblock', null, false).setAttribute('title', mxResources.get('block'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_BLOCK_THIN, 1], 'geIcon geSprite geSprite-startblockthin', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['async', 1], 'geIcon geSprite geSprite-startasync', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_OVAL, 1], 'geIcon geSprite geSprite-startoval', null, false).setAttribute('title', mxResources.get('oval'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_DIAMOND, 1], 'geIcon geSprite geSprite-startdiamond', null, false).setAttribute('title', mxResources.get('diamond'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_DIAMOND_THIN, 1], 'geIcon geSprite geSprite-startthindiamond', null, false).setAttribute('title', mxResources.get('diamondThin'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_CLASSIC, 0], 'geIcon geSprite geSprite-startclassictrans', null, false).setAttribute('title', mxResources.get('classic'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_CLASSIC_THIN, 0], 'geIcon geSprite geSprite-startclassicthintrans', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_BLOCK, 0], 'geIcon geSprite geSprite-startblocktrans', null, false).setAttribute('title', mxResources.get('block'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_BLOCK_THIN, 0], 'geIcon geSprite geSprite-startblockthintrans', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['async', 0], 'geIcon geSprite geSprite-startasynctrans', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_OVAL, 0], 'geIcon geSprite geSprite-startovaltrans', null, false).setAttribute('title', mxResources.get('oval'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_DIAMOND, 0], 'geIcon geSprite geSprite-startdiamondtrans', null, false).setAttribute('title', mxResources.get('diamond'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], [mxConstants.ARROW_DIAMOND_THIN, 0], 'geIcon geSprite geSprite-startthindiamondtrans', null, false).setAttribute('title', mxResources.get('diamondThin'));
				
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['dash', 0], 'geIcon geSprite geSprite-startdash', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['cross', 0], 'geIcon geSprite geSprite-startcross', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['circlePlus', 0], 'geIcon geSprite geSprite-startcircleplus', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['circle', 1], 'geIcon geSprite geSprite-startcircle', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['ERone', 0], 'geIcon geSprite geSprite-starterone', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['ERmandOne', 0], 'geIcon geSprite geSprite-starteronetoone', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['ERmany', 0], 'geIcon geSprite geSprite-startermany', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['ERoneToMany', 0], 'geIcon geSprite geSprite-starteronetomany', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['ERzeroToOne', 1], 'geIcon geSprite geSprite-starteroneopt', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW, 'startFill'], ['ERzeroToMany', 1], 'geIcon geSprite geSprite-startermanyopt', null, false);
			}
			else
			{
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_STARTARROW], [mxConstants.ARROW_BLOCK], 'geIcon geSprite geSprite-startblocktrans', null, false).setAttribute('title', mxResources.get('block'));
			}
		}
	}));

	var lineEnd = this.editorUi.toolbar.addMenuFunctionInContainer(stylePanel2, 'geSprite-endclassic', mxResources.get('lineend'), false, mxUtils.bind(this, function(menu)
	{
		if (ss.style.shape == 'connector' || ss.style.shape == 'flexArrow')
		{
			this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.NONE, 0], 'geIcon geSprite geSprite-noarrow', null, false).setAttribute('title', mxResources.get('none'));
	
			if (ss.style.shape == 'connector')
			{
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_CLASSIC, 1], 'geIcon geSprite geSprite-endclassic', null, false).setAttribute('title', mxResources.get('classic'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_CLASSIC_THIN, 1], 'geIcon geSprite geSprite-endclassicthin', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_OPEN, 0], 'geIcon geSprite geSprite-endopen', null, false).setAttribute('title', mxResources.get('openArrow'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_OPEN_THIN, 0], 'geIcon geSprite geSprite-endopenthin', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['openAsync', 0], 'geIcon geSprite geSprite-endopenasync', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_BLOCK, 1], 'geIcon geSprite geSprite-endblock', null, false).setAttribute('title', mxResources.get('block'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_BLOCK_THIN, 1], 'geIcon geSprite geSprite-endblockthin', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['async', 1], 'geIcon geSprite geSprite-endasync', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_OVAL, 1], 'geIcon geSprite geSprite-endoval', null, false).setAttribute('title', mxResources.get('oval'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_DIAMOND, 1], 'geIcon geSprite geSprite-enddiamond', null, false).setAttribute('title', mxResources.get('diamond'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_DIAMOND_THIN, 1], 'geIcon geSprite geSprite-endthindiamond', null, false).setAttribute('title', mxResources.get('diamondThin'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_CLASSIC, 0], 'geIcon geSprite geSprite-endclassictrans', null, false).setAttribute('title', mxResources.get('classic'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_CLASSIC_THIN, 0], 'geIcon geSprite geSprite-endclassicthintrans', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_BLOCK, 0], 'geIcon geSprite geSprite-endblocktrans', null, false).setAttribute('title', mxResources.get('block'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_BLOCK_THIN, 0], 'geIcon geSprite geSprite-endblockthintrans', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['async', 0], 'geIcon geSprite geSprite-endasynctrans', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_OVAL, 0], 'geIcon geSprite geSprite-endovaltrans', null, false).setAttribute('title', mxResources.get('oval'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_DIAMOND, 0], 'geIcon geSprite geSprite-enddiamondtrans', null, false).setAttribute('title', mxResources.get('diamond'));
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], [mxConstants.ARROW_DIAMOND_THIN, 0], 'geIcon geSprite geSprite-endthindiamondtrans', null, false).setAttribute('title', mxResources.get('diamondThin'));

				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['dash', 0], 'geIcon geSprite geSprite-enddash', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['cross', 0], 'geIcon geSprite geSprite-endcross', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['circlePlus', 0], 'geIcon geSprite geSprite-endcircleplus', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['circle', 1], 'geIcon geSprite geSprite-endcircle', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['ERone', 0], 'geIcon geSprite geSprite-enderone', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['ERmandOne', 0], 'geIcon geSprite geSprite-enderonetoone', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['ERmany', 0], 'geIcon geSprite geSprite-endermany', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['ERoneToMany', 0], 'geIcon geSprite geSprite-enderonetomany', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['ERzeroToOne', 1], 'geIcon geSprite geSprite-enderoneopt', null, false);
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW, 'endFill'], ['ERzeroToMany', 1], 'geIcon geSprite geSprite-endermanyopt', null, false);
			}
			else
			{
				this.editorUi.menus.edgeStyleChange(menu, '', [mxConstants.STYLE_ENDARROW], [mxConstants.ARROW_BLOCK], 'geIcon geSprite geSprite-endblocktrans', null, false).setAttribute('title', mxResources.get('block'));
			}
		}
	}));

	this.addArrow(edgeShape, 8);
	this.addArrow(edgeStyle);
	this.addArrow(lineStart);
	this.addArrow(lineEnd);
	
	var symbol = this.addArrow(pattern, 9);
	symbol.className = 'geIcon';
	symbol.style.width = '84px';
	
	var altSymbol = this.addArrow(altPattern, 9);
	altSymbol.className = 'geIcon';
	altSymbol.style.width = '22px';
	
	var solid = document.createElement('div');
	solid.style.width = '85px';
	solid.style.height = '1px';
	solid.style.borderBottom = '1px solid black';
	solid.style.marginBottom = '9px';
	symbol.appendChild(solid);
	
	var altSolid = document.createElement('div');
	altSolid.style.width = '23px';
	altSolid.style.height = '1px';
	altSolid.style.borderBottom = '1px solid black';
	altSolid.style.marginBottom = '9px';
	altSymbol.appendChild(altSolid);

	pattern.style.height = '15px';
	altPattern.style.height = '15px';
	edgeShape.style.height = '15px';
	edgeStyle.style.height = '17px';
	lineStart.style.marginLeft = '3px';
	lineStart.style.height = '17px';
	lineEnd.style.marginLeft = '3px';
	lineEnd.style.height = '17px';

	container.appendChild(colorPanel);
	container.appendChild(altStylePanel);
	container.appendChild(stylePanel);

	var arrowPanel = stylePanel.cloneNode(false);
	arrowPanel.style.paddingBottom = '6px';
	arrowPanel.style.paddingTop = '4px';
	arrowPanel.style.fontWeight = 'normal';
	
	var span = document.createElement('div');
	span.style.position = 'absolute';
	span.style.marginLeft = '3px';
	span.style.marginBottom = '12px';
	span.style.marginTop = '2px';
	span.style.fontWeight = 'normal';
	span.style.width = '76px';
	
	mxUtils.write(span, mxResources.get('lineend'));
	arrowPanel.appendChild(span);
	
	var endSpacingUpdate, endSizeUpdate;
	var endSpacing = this.addUnitInput(arrowPanel, 'pt', 74, 33, function()
	{
		endSpacingUpdate.apply(this, arguments);
	});
	var endSize = this.addUnitInput(arrowPanel, 'pt', 20, 33, function()
	{
		endSizeUpdate.apply(this, arguments);
	});

	mxUtils.br(arrowPanel);
	
	var spacer = document.createElement('div');
	spacer.style.height = '8px';
	arrowPanel.appendChild(spacer);
	
	span = span.cloneNode(false);
	mxUtils.write(span, mxResources.get('linestart'));
	arrowPanel.appendChild(span);
	
	var startSpacingUpdate, startSizeUpdate;
	var startSpacing = this.addUnitInput(arrowPanel, 'pt', 74, 33, function()
	{
		startSpacingUpdate.apply(this, arguments);
	});
	var startSize = this.addUnitInput(arrowPanel, 'pt', 20, 33, function()
	{
		startSizeUpdate.apply(this, arguments);
	});

	mxUtils.br(arrowPanel);
	this.addLabel(arrowPanel, mxResources.get('spacing'), 74, 50);
	this.addLabel(arrowPanel, mxResources.get('size'), 20, 50);
	mxUtils.br(arrowPanel);
	
	var perimeterPanel = colorPanel.cloneNode(false);
	perimeterPanel.style.fontWeight = 'normal';
	perimeterPanel.style.position = 'relative';
	perimeterPanel.style.paddingLeft = '16px'
	perimeterPanel.style.marginBottom = '2px';
	perimeterPanel.style.marginTop = '6px';
	perimeterPanel.style.borderWidth = '0px';
	perimeterPanel.style.paddingBottom = '18px';
	
	var span = document.createElement('div');
	span.style.position = 'absolute';
	span.style.marginLeft = '3px';
	span.style.marginBottom = '12px';
	span.style.marginTop = '1px';
	span.style.fontWeight = 'normal';
	span.style.width = '120px';
	mxUtils.write(span, mxResources.get('perimeter'));
	perimeterPanel.appendChild(span);
	
	var perimeterUpdate;
	var perimeterSpacing = this.addUnitInput(perimeterPanel, 'pt', 20, 41, function()
	{
		perimeterUpdate.apply(this, arguments);
	});

	if (ss.edges.length == graph.getSelectionCount())
	{
		container.appendChild(stylePanel2);
		
		if (mxClient.IS_QUIRKS)
		{
			mxUtils.br(container);
			mxUtils.br(container);
		}
		
		container.appendChild(arrowPanel);
	}
	else if (ss.vertices.length == graph.getSelectionCount())
	{
		if (mxClient.IS_QUIRKS)
		{
			mxUtils.br(container);
		}
		
		container.appendChild(perimeterPanel);
	}
	
	var listener = mxUtils.bind(this, function(sender, evt, force)
	{
		ss = this.format.getSelectionState();
		var color = mxUtils.getValue(ss.style, strokeKey, null);

		if (force || document.activeElement != input)
		{
			var tmp = parseInt(mxUtils.getValue(ss.style, mxConstants.STYLE_STROKEWIDTH, 1));
			input.value = (isNaN(tmp)) ? '' : tmp + ' pt';
		}
		
		if (force || document.activeElement != altInput)
		{
			var tmp = parseInt(mxUtils.getValue(ss.style, mxConstants.STYLE_STROKEWIDTH, 1));
			altInput.value = (isNaN(tmp)) ? '' : tmp + ' pt';
		}
		
		styleSelect.style.visibility = (ss.style.shape == 'connector') ? '' : 'hidden';
		
		if (mxUtils.getValue(ss.style, mxConstants.STYLE_CURVED, null) == '1')
		{
			styleSelect.value = 'curved';
		}
		else if (mxUtils.getValue(ss.style, mxConstants.STYLE_ROUNDED, null) == '1')
		{
			styleSelect.value = 'rounded';
		}
		
		if (mxUtils.getValue(ss.style, mxConstants.STYLE_DASHED, null) == '1')
		{
			if (mxUtils.getValue(ss.style, mxConstants.STYLE_DASH_PATTERN, null) == null)
			{
				solid.style.borderBottom = '1px dashed black';
			}
			else
			{
				solid.style.borderBottom = '1px dotted black';
			}
		}
		else
		{
			solid.style.borderBottom = '1px solid black';
		}
		
		altSolid.style.borderBottom = solid.style.borderBottom;
		
		// Updates toolbar icon for edge style
		var edgeStyleDiv = edgeStyle.getElementsByTagName('div')[0];
		var es = mxUtils.getValue(ss.style, mxConstants.STYLE_EDGE, null);
		
		if (mxUtils.getValue(ss.style, mxConstants.STYLE_NOEDGESTYLE, null) == '1')
		{
			es = null;
		}

		if (es == 'orthogonalEdgeStyle' && mxUtils.getValue(ss.style, mxConstants.STYLE_CURVED, null) == '1')
		{
			edgeStyleDiv.className = 'geSprite geSprite-curved';
		}
		else if (es == 'straight' || es == 'none' || es == null)
		{
			edgeStyleDiv.className = 'geSprite geSprite-straight';
		}
		else if (es == 'entityRelationEdgeStyle')
		{
			edgeStyleDiv.className = 'geSprite geSprite-entity';
		}
		else if (es == 'elbowEdgeStyle')
		{
			edgeStyleDiv.className = 'geSprite ' + ((mxUtils.getValue(ss.style,
				mxConstants.STYLE_ELBOW, null) == 'vertical') ?
				'geSprite-verticalelbow' : 'geSprite-horizontalelbow');
		}
		else if (es == 'isometricEdgeStyle')
		{
			edgeStyleDiv.className = 'geSprite ' + ((mxUtils.getValue(ss.style,
				mxConstants.STYLE_ELBOW, null) == 'vertical') ?
				'geSprite-verticalisometric' : 'geSprite-horizontalisometric');
		}
		else
		{
			edgeStyleDiv.className = 'geSprite geSprite-orthogonal';
		}
		
		// Updates icon for edge shape
		var edgeShapeDiv = edgeShape.getElementsByTagName('div')[0];
		
		if (ss.style.shape == 'link')
		{
			edgeShapeDiv.className = 'geSprite geSprite-linkedge';
		}
		else if (ss.style.shape == 'flexArrow')
		{
			edgeShapeDiv.className = 'geSprite geSprite-arrow';
		}
		else if (ss.style.shape == 'arrow')
		{
			edgeShapeDiv.className = 'geSprite geSprite-simplearrow';
		}
		else
		{
			edgeShapeDiv.className = 'geSprite geSprite-connection';
		}
		
		if (ss.edges.length == graph.getSelectionCount())
		{
			altStylePanel.style.display = '';
			stylePanel.style.display = 'none';
		}
		else
		{
			altStylePanel.style.display = 'none';
			stylePanel.style.display = '';
		}
		
		function updateArrow(marker, fill, elt, prefix)
		{
			var markerDiv = elt.getElementsByTagName('div')[0];
			
			markerDiv.className = ui.getCssClassForMarker(prefix, ss.style.shape, marker, fill);
			
			return markerDiv;
		};
		
		var sourceDiv = updateArrow(mxUtils.getValue(ss.style, mxConstants.STYLE_STARTARROW, null),
				mxUtils.getValue(ss.style, 'startFill', '1'), lineStart, 'start');
		var targetDiv = updateArrow(mxUtils.getValue(ss.style, mxConstants.STYLE_ENDARROW, null),
				mxUtils.getValue(ss.style, 'endFill', '1'), lineEnd, 'end');

		// Special cases for markers
		if (ss.style.shape == 'arrow')
		{
			sourceDiv.className = 'geSprite geSprite-noarrow';
			targetDiv.className = 'geSprite geSprite-endblocktrans';
		}
		else if (ss.style.shape == 'link')
		{
			sourceDiv.className = 'geSprite geSprite-noarrow';
			targetDiv.className = 'geSprite geSprite-noarrow';
		}

		mxUtils.setOpacity(edgeStyle, (ss.style.shape == 'arrow') ? 30 : 100);			
		
		if (ss.style.shape != 'connector' && ss.style.shape != 'flexArrow')
		{
			mxUtils.setOpacity(lineStart, 30);
			mxUtils.setOpacity(lineEnd, 30);
		}
		else
		{
			mxUtils.setOpacity(lineStart, 100);
			mxUtils.setOpacity(lineEnd, 100);
		}

		if (force || document.activeElement != startSize)
		{
			var tmp = parseInt(mxUtils.getValue(ss.style, mxConstants.STYLE_STARTSIZE, mxConstants.DEFAULT_MARKERSIZE));
			startSize.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
		
		if (force || document.activeElement != startSpacing)
		{
			var tmp = parseInt(mxUtils.getValue(ss.style, mxConstants.STYLE_SOURCE_PERIMETER_SPACING, 0));
			startSpacing.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}

		if (force || document.activeElement != endSize)
		{
			var tmp = parseInt(mxUtils.getValue(ss.style, mxConstants.STYLE_ENDSIZE, mxConstants.DEFAULT_MARKERSIZE));
			endSize.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
		
		if (force || document.activeElement != startSpacing)
		{
			var tmp = parseInt(mxUtils.getValue(ss.style, mxConstants.STYLE_TARGET_PERIMETER_SPACING, 0));
			endSpacing.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
		
		if (force || document.activeElement != perimeterSpacing)
		{
			var tmp = parseInt(mxUtils.getValue(ss.style, mxConstants.STYLE_PERIMETER_SPACING, 0));
			perimeterSpacing.value = (isNaN(tmp)) ? '' : tmp  + ' pt';
		}
	});
	
	startSizeUpdate = this.installInputHandler(startSize, mxConstants.STYLE_STARTSIZE, mxConstants.DEFAULT_MARKERSIZE, 0, 999, ' pt');
	startSpacingUpdate = this.installInputHandler(startSpacing, mxConstants.STYLE_SOURCE_PERIMETER_SPACING, 0, -999, 999, ' pt');
	endSizeUpdate = this.installInputHandler(endSize, mxConstants.STYLE_ENDSIZE, mxConstants.DEFAULT_MARKERSIZE, 0, 999, ' pt');
	endSpacingUpdate = this.installInputHandler(endSpacing, mxConstants.STYLE_TARGET_PERIMETER_SPACING, 0, -999, 999, ' pt');
	perimeterUpdate = this.installInputHandler(perimeterSpacing, mxConstants.STYLE_PERIMETER_SPACING, 0, 0, 999, ' pt');

	this.addKeyHandler(input, listener);
	this.addKeyHandler(startSize, listener);
	this.addKeyHandler(startSpacing, listener);
	this.addKeyHandler(endSize, listener);
	this.addKeyHandler(endSpacing, listener);
	this.addKeyHandler(perimeterSpacing, listener);

	graph.getModel().addListener(mxEvent.CHANGE, listener);
	this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
	listener();

	return container;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
StyleFormatPanel.prototype.addEffects = function(div)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	var ss = this.format.getSelectionState();
	
	div.style.paddingTop = '0px';
	div.style.paddingBottom = '2px';

	var table = document.createElement('table');

	if (mxClient.IS_QUIRKS)
	{
		table.style.fontSize = '1em';
	}

	table.style.width = '100%';
	table.style.fontWeight = 'bold';
	table.style.paddingRight = '20px';
	var tbody = document.createElement('tbody');
	var row = document.createElement('tr');
	row.style.padding = '0px';
	var left = document.createElement('td');
	left.style.padding = '0px';
	left.style.width = '50%';
	left.setAttribute('valign', 'top');
	
	var right = left.cloneNode(true);
	right.style.paddingLeft = '8px';
	row.appendChild(left);
	row.appendChild(right);
	tbody.appendChild(row);
	table.appendChild(tbody);
	div.appendChild(table);

	var current = left;
	var count = 0;
	
	var addOption = mxUtils.bind(this, function(label, key, defaultValue)
	{
		var opt = this.createCellOption(label, key, defaultValue);
		opt.style.width = '100%';
		current.appendChild(opt);
		current = (current == left) ? right : left;
		count++;
	});

	var listener = mxUtils.bind(this, function(sender, evt, force)
	{
		ss = this.format.getSelectionState();
		
		left.innerHTML = '';
		right.innerHTML = '';
		current = left;
		
		if (ss.rounded)
		{
			addOption(mxResources.get('rounded'), mxConstants.STYLE_ROUNDED, 0);
		}
		
		if (ss.style.shape == 'swimlane')
		{
			addOption(mxResources.get('divider'), 'swimlaneLine', 1);
		}

		if (!ss.containsImage)
		{
			addOption(mxResources.get('shadow'), mxConstants.STYLE_SHADOW, 0);
		}
		
		if (ss.glass)
		{
			addOption(mxResources.get('glass'), mxConstants.STYLE_GLASS, 0);
		}

		if (ss.comic)
		{
			addOption(mxResources.get('comic'), 'comic', 0);
		}
		
		if (count == 0)
		{
			div.style.display = 'none';
		}
	});
	
	graph.getModel().addListener(mxEvent.CHANGE, listener);
	this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
	listener();

	return div;
}

/**
 * Adds the label menu items to the given menu and parent.
 */
StyleFormatPanel.prototype.addStyleOps = function(div)
{
	div.style.paddingTop = '10px';
	div.style.paddingBottom = '10px';
	
	var btn = mxUtils.button(mxResources.get('setAsDefaultStyle'), mxUtils.bind(this, function(evt)
	{
		this.editorUi.actions.get('setAsDefaultStyle').funct();
	}));
	
	btn.setAttribute('title', mxResources.get('setAsDefaultStyle') + ' (' + this.editorUi.actions.get('setAsDefaultStyle').shortcut + ')');
	btn.style.width = '202px';
	div.appendChild(btn);

	return div;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
DiagramFormatPanel = function(format, editorUi, container)
{
	BaseFormatPanel.call(this, format, editorUi, container);
	this.init();
};

mxUtils.extend(DiagramFormatPanel, BaseFormatPanel);

/**
 * Specifies if the background image option should be shown. Default is true.
 */
DiagramFormatPanel.prototype.showBackgroundImageOption = true;

/**
 * Adds the label menu items to the given menu and parent.
 */
DiagramFormatPanel.prototype.init = function()
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;

	this.container.appendChild(this.addView(this.createPanel()));

	if (graph.isEnabled())
	{
		this.container.appendChild(this.addOptions(this.createPanel()));
		this.container.appendChild(this.addPaperSize(this.createPanel()));
		this.container.appendChild(this.addStyleOps(this.createPanel()));
	}
};

/**
 * Adds the label menu items to the given menu and parent.
 */
DiagramFormatPanel.prototype.addView = function(div)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	
	div.appendChild(this.createTitle(mxResources.get('view')));
	
	// Grid
	this.addGridOption(div);

	if (graph.isEnabled())
	{
		// Guides
		div.appendChild(this.createOption(mxResources.get('guides'), function()
		{
			return graph.graphHandler.guidesEnabled;
		}, function(checked)
		{
			ui.actions.get('guides').funct();
		},
		{
			install: function(apply)
			{
				this.listener = function()
				{
					apply(graph.graphHandler.guidesEnabled);
				};
				
				ui.addListener('guidesEnabledChanged', this.listener);
			},
			destroy: function()
			{
				ui.removeListener(this.listener);
			}
		}));
		
		// Page View
		div.appendChild(this.createOption(mxResources.get('pageView'), function()
		{
			return graph.pageVisible;
		}, function(checked)
		{
			ui.actions.get('pageView').funct();
		},
		{
			install: function(apply)
			{
				this.listener = function()
				{
					apply(graph.pageVisible);
				};
				
				ui.addListener('pageViewChanged', this.listener);
			},
			destroy: function()
			{
				ui.removeListener(this.listener);
			}
		}));
		
		// Background
		var bg = this.createColorOption(mxResources.get('background'), function()
		{
			return graph.background;
		}, function(color)
		{
			ui.setBackgroundColor(color);
		}, '#ffffff',
		{
			install: function(apply)
			{
				this.listener = function()
				{
					apply(graph.background);
				};
				
				ui.addListener('backgroundColorChanged', this.listener);
			},
			destroy: function()
			{
				ui.removeListener(this.listener);
			}
		});
		
		if (this.showBackgroundImageOption)
		{
			var btn = mxUtils.button(mxResources.get('image'), function(evt)
			{
				ui.showBackgroundImageDialog();
				mxEvent.consume(evt);
			})
		
			btn.style.position = 'absolute';
			btn.className = 'geColorBtn';
			btn.style.marginTop = '-4px';
			btn.style.paddingBottom = (document.documentMode == 11 || mxClient.IS_MT) ? '0px' : '2px';
			btn.style.height = '22px';
			btn.style.right = (mxClient.IS_QUIRKS) ? '52px' : '72px';
			btn.style.width = '56px';
		
			bg.appendChild(btn);
		}
		
		div.appendChild(bg);
	}
	
	return div;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
DiagramFormatPanel.prototype.addOptions = function(div)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	
	div.appendChild(this.createTitle(mxResources.get('options')));	

	if (graph.isEnabled())
	{
		// Connection arrows
		div.appendChild(this.createOption(mxResources.get('connectionArrows'), function()
		{
			return graph.connectionArrowsEnabled;
		}, function(checked)
		{
			ui.actions.get('connectionArrows').funct();
		},
		{
			install: function(apply)
			{
				this.listener = function()
				{
					apply(graph.connectionArrowsEnabled);
				};
				
				ui.addListener('connectionArrowsChanged', this.listener);
			},
			destroy: function()
			{
				ui.removeListener(this.listener);
			}
		}));
		
		// Connection points
		div.appendChild(this.createOption(mxResources.get('connectionPoints'), function()
		{
			return graph.connectionHandler.isEnabled();
		}, function(checked)
		{
			ui.actions.get('connectionPoints').funct();
		},
		{
			install: function(apply)
			{
				this.listener = function()
				{
					apply(graph.connectionHandler.isEnabled());
				};
				
				ui.addListener('connectionPointsChanged', this.listener);
			},
			destroy: function()
			{
				ui.removeListener(this.listener);
			}
		}));
	}

	return div;
};

/**
 * 
 */
DiagramFormatPanel.prototype.addGridOption = function(container)
{
	var ui = this.editorUi;
	var graph = ui.editor.graph;
	
	var input = document.createElement('input');
	input.style.position = 'absolute';
	input.style.textAlign = 'right';
	input.style.width = '38px';
	input.value = graph.getGridSize() + ' pt';
	
	var stepper = this.createStepper(input, update);
	input.style.display = (graph.isGridEnabled()) ? '' : 'none';
	stepper.style.display = input.style.display;

	mxEvent.addListener(input, 'keydown', function(e)
	{
		if (e.keyCode == 13)
		{
			graph.container.focus();
			mxEvent.consume(e);
		}
		else if (e.keyCode == 27)
		{
			input.value = graph.getGridSize();
			graph.container.focus();
			mxEvent.consume(e);
		}
	});
	
	function update(evt)
	{
		var value = parseInt(input.value);
		value = Math.max(1, (isNaN(value)) ? 10 : value);
		
		if (value != graph.getGridSize())
		{
			graph.setGridSize(value)
		}

		input.value = value + ' pt';
		mxEvent.consume(evt);
	};

	mxEvent.addListener(input, 'blur', update);
	mxEvent.addListener(input, 'change', update);
	
	if (mxClient.IS_SVG)
	{
		input.style.marginTop = '-2px';
		input.style.right = '84px';
		stepper.style.marginTop = '-16px';
		stepper.style.right = '72px';
	
		var panel = this.createColorOption(mxResources.get('grid'), function()
		{
			var color = graph.view.gridColor;

			return (graph.isGridEnabled()) ? color : null;
		}, function(color)
		{
			if (color == mxConstants.NONE)
			{
				graph.setGridEnabled(false);
				ui.fireEvent(new mxEventObject('gridEnabledChanged'));
			}
			else
			{
				graph.setGridEnabled(true);
				ui.setGridColor(color);
			}

			input.style.display = (graph.isGridEnabled()) ? '' : 'none';
			stepper.style.display = input.style.display;
		}, '#e0e0e0',
		{
			install: function(apply)
			{
				this.listener = function()
				{
					apply((graph.isGridEnabled()) ? graph.view.gridColor : null);
				};
				
				ui.addListener('gridColorChanged', this.listener);
				ui.addListener('gridEnabledChanged', this.listener);
			},
			destroy: function()
			{
				ui.removeListener(this.listener);
			}
		});

		panel.appendChild(input);
		panel.appendChild(stepper);
		container.appendChild(panel);
	}
	else
	{
		input.style.marginTop = '2px';
		input.style.right = '32px';
		stepper.style.marginTop = '2px';
		stepper.style.right = '20px';
		
		container.appendChild(input);
		container.appendChild(stepper);
		
		container.appendChild(this.createOption(mxResources.get('grid'), function()
		{
			return graph.isGridEnabled();
		}, function(checked)
		{
			graph.setGridEnabled(checked);
			
			if (graph.isGridEnabled())
			{
				graph.view.gridColor = '#e0e0e0';
			}
			
			ui.fireEvent(new mxEventObject('gridEnabledChanged'));
		},
		{
			install: function(apply)
			{
				this.listener = function()
				{
					input.style.display = (graph.isGridEnabled()) ? '' : 'none';
					stepper.style.display = input.style.display;
					
					apply(graph.isGridEnabled());
				};
				
				ui.addListener('gridEnabledChanged', this.listener);
			},
			destroy: function()
			{
				ui.removeListener(this.listener);
			}
		}));
	}
};

/**
 * Adds the label menu items to the given menu and parent.
 */
DiagramFormatPanel.prototype.addDocumentProperties = function(div)
{
	// Hook for subclassers
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	
	div.appendChild(this.createTitle(mxResources.get('options')));

	return div;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
DiagramFormatPanel.prototype.addPaperSize = function(div)
{
	var ui = this.editorUi;
	var editor = ui.editor;
	var graph = editor.graph;
	
	div.appendChild(this.createTitle(mxResources.get('paperSize')));

	var accessor = PageSetupDialog.addPageFormatPanel(div, 'formatpanel', graph.pageFormat, function(pageFormat)
	{
		if (graph.pageFormat == null || graph.pageFormat.width != pageFormat.width || graph.pageFormat.height != pageFormat.height)
		{
			ui.setPageFormat(pageFormat);
		}
	});
	
	this.addKeyHandler(accessor.widthInput, function()
	{
		console.log('here', graph.pageFormat);
		accessor.set(graph.pageFormat);
	});
-	this.addKeyHandler(accessor.heightInput, function()
	{
		accessor.set(graph.pageFormat);	
	});
	
	var listener = function()
	{
		accessor.set(graph.pageFormat);
	};
	
	ui.addListener('pageFormatChanged', listener);
	this.listeners.push({destroy: function() { ui.removeListener(listener); }});
	
	graph.getModel().addListener(mxEvent.CHANGE, listener);
	this.listeners.push({destroy: function() { graph.getModel().removeListener(listener); }});
	
	return div;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
DiagramFormatPanel.prototype.addStyleOps = function(div)
{
	var btn = mxUtils.button(mxResources.get('editData'), mxUtils.bind(this, function(evt)
	{
		this.editorUi.actions.get('editData').funct();
	}));
	
	btn.setAttribute('title', mxResources.get('editData') + ' (' + this.editorUi.actions.get('editData').shortcut + ')');
	btn.style.width = '202px';
	btn.style.marginBottom = '2px';
	div.appendChild(btn);

	mxUtils.br(div);

	btn = mxUtils.button(mxResources.get('clearDefaultStyle'), mxUtils.bind(this, function(evt)
	{
		this.editorUi.actions.get('clearDefaultStyle').funct();
	}));
	
	btn.setAttribute('title', mxResources.get('clearDefaultStyle') + ' (' + this.editorUi.actions.get('clearDefaultStyle').shortcut + ')');
	btn.style.width = '202px';
	div.appendChild(btn);

	return div;
};

/**
 * Adds the label menu items to the given menu and parent.
 */
DiagramFormatPanel.prototype.destroy = function()
{
	BaseFormatPanel.prototype.destroy.apply(this, arguments);
	
	if (this.gridEnabledListener)
	{
		this.editorUi.removeListener(this.gridEnabledListener);
		this.gridEnabledListener = null;
	}
};
