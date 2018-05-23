local myToast = toasts.create("(╯°□°)╯︵ ┻━┻", "How dare you try to eat me", 200)

items.add("rbc/toast", "Very delicious", 1, nil, nil, function(x, y, layer, mouseX, mouseY, player, instance)
    toasts.display(myToast)
end)
