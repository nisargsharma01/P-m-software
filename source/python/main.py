#!/usr/bin/env python3
"""Small, persistent command-line pharmacy inventory manager."""

import json
from pathlib import Path

DATA_FILE = Path("inventory.json")


def load_inventory():
    if not DATA_FILE.exists():
        return []
    try:
        data = json.loads(DATA_FILE.read_text(encoding="utf-8"))
        if isinstance(data, list):
            return [item for item in data if valid_item(item)]
    except (OSError, json.JSONDecodeError):
        print(f"Could not read {DATA_FILE}; starting with empty inventory.")
    return []


def valid_item(item):
    return (
        isinstance(item, dict)
        and isinstance(item.get("id"), int)
        and isinstance(item.get("name"), str)
        and isinstance(item.get("quantity"), int)
        and item["quantity"] >= 0
        and isinstance(item.get("price"), (int, float))
        and item["price"] >= 0
    )


def save_inventory(inventory):
    try:
        DATA_FILE.write_text(json.dumps(inventory, indent=2) + "\n", encoding="utf-8")
    except OSError as error:
        print(f"Could not save inventory: {error}")


def read_integer(prompt, minimum):
    while True:
        try:
            value = int(input(prompt).strip())
            if value >= minimum:
                return value
        except ValueError:
            pass
        print(f"Enter a whole number of at least {minimum}.")


def read_price():
    while True:
        try:
            value = float(input("Price: ").strip())
            if value >= 0:
                return value
        except ValueError:
            pass
        print("Enter a valid non-negative price.")


def list_inventory(inventory):
    if not inventory:
        print("No medicines yet.")
        return
    print(f"{'ID':<5} {'Medicine':<30} {'Quantity':>10} {'Price':>12}")
    for item in inventory:
        print(f"{item['id']:<5} {item['name']:<30} {item['quantity']:>10} {item['price']:>12.2f}")


def add_medicine(inventory):
    name = input("Medicine name: ").strip()
    if not name:
        print("A medicine name is required.")
        return
    next_id = max((item["id"] for item in inventory), default=0) + 1
    inventory.append({
        "id": next_id,
        "name": name,
        "quantity": read_integer("Quantity: ", 0),
        "price": read_price(),
    })
    save_inventory(inventory)
    print("Medicine added.")


def record_sale(inventory):
    list_inventory(inventory)
    if not inventory:
        return
    medicine_id = read_integer("Medicine ID to sell: ", 1)
    item = next((entry for entry in inventory if entry["id"] == medicine_id), None)
    if item is None:
        print("Medicine not found.")
        return
    amount = read_integer("Quantity sold: ", 1)
    if amount > item["quantity"]:
        print(f"Not enough stock. Available: {item['quantity']}")
        return
    item["quantity"] -= amount
    save_inventory(inventory)
    print(f"Sale recorded. Total: {amount * item['price']:.2f}")


def main():
    inventory = load_inventory()
    print("Pharmacy inventory manager (Python)")
    while True:
        print("\n1) List stock  2) Add medicine  3) Record sale  4) Quit")
        choice = input("Choose: ").strip()
        if choice == "1":
            list_inventory(inventory)
        elif choice == "2":
            add_medicine(inventory)
        elif choice == "3":
            record_sale(inventory)
        elif choice == "4":
            save_inventory(inventory)
            print("Inventory saved. Goodbye!")
            break
        else:
            print("Please enter 1, 2, 3, or 4.")


if __name__ == "__main__":
    main()
